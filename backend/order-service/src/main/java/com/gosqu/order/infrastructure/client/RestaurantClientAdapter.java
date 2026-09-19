package com.gosqu.order.infrastructure.client;

import com.gosqu.order.application.port.out.RestaurantClientPort;
import com.gosqu.order.domain.exception.RestaurantServiceUnavailableException;
import com.gosqu.order.infrastructure.client.dto.MenuClientResponse;
import com.gosqu.order.infrastructure.client.dto.MenuItemSnapshot;
import com.gosqu.order.infrastructure.client.dto.RestaurantSnapshot;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantClientAdapter implements RestaurantClientPort {

    private final RestClient restClient;

    RestaurantClientAdapter(RestClient.Builder restClientBuilder,
                             @Value("${services.restaurant-service.url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    @CircuitBreaker(name = "restaurant-service")
    @Bulkhead(name = "restaurant-service")
    @Retry(name = "restaurant-service", fallbackMethod = "getRestaurantFallback")
    public RestaurantSnapshot getRestaurant(UUID restaurantId) {
        log.debug("action=get_restaurant restaurantId={}", restaurantId);
        return restClient.get().uri("/restaurants/{id}", restaurantId).retrieve().body(RestaurantSnapshot.class);
    }

    @Override
    @CircuitBreaker(name = "restaurant-service")
    @Bulkhead(name = "restaurant-service")
    @Retry(name = "restaurant-service", fallbackMethod = "getMenuItemsFallback")
    public List<MenuItemSnapshot> getMenuItems(UUID restaurantId, List<UUID> itemIds) {
        log.debug("action=get_menu_items restaurantId={} count={}", restaurantId, itemIds.size());
        Set<UUID> requested = Set.copyOf(itemIds);
        MenuClientResponse menu = restClient.get().uri("/restaurants/{id}/menu", restaurantId).retrieve().body(MenuClientResponse.class);
        if (menu == null || menu.categories() == null) { return List.of(); }
        return menu.categories().stream()
                .flatMap(cat -> cat.items() != null ? cat.items().stream() : java.util.stream.Stream.empty())
                .filter(item -> requested.contains(item.id()))
                .toList();
    }

    // Bardziej precyzyjna sygnatura wygrywa w resilience4j — 4xx (np. restauracja nie istnieje)
    // ma zostać przepuszczone dalej, a nie zamienione na 503.
    private RestaurantSnapshot getRestaurantFallback(UUID restaurantId, HttpClientErrorException ex) {
        throw ex;
    }

    private RestaurantSnapshot getRestaurantFallback(UUID restaurantId, Throwable ex) {
        log.warn("action=restaurant_service_unavailable restaurantId={} cause={}", restaurantId, ex.toString());
        throw new RestaurantServiceUnavailableException(restaurantId, ex);
    }

    private List<MenuItemSnapshot> getMenuItemsFallback(UUID restaurantId, List<UUID> itemIds, HttpClientErrorException ex) {
        throw ex;
    }

    private List<MenuItemSnapshot> getMenuItemsFallback(UUID restaurantId, List<UUID> itemIds, Throwable ex) {
        log.warn("action=restaurant_service_unavailable restaurantId={} cause={}", restaurantId, ex.toString());
        throw new RestaurantServiceUnavailableException(restaurantId, ex);
    }
}
