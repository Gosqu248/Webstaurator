package com.gosqu.order.infrastructure.client;

import com.gosqu.order.application.port.out.RestaurantClientPort;
import com.gosqu.order.infrastructure.client.dto.MenuClientResponse;
import com.gosqu.order.infrastructure.client.dto.MenuItemSnapshot;
import com.gosqu.order.infrastructure.client.dto.RestaurantSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantClientAdapter implements RestaurantClientPort {

    private final RestClient restClient;

    RestaurantClientAdapter(@Value("${services.restaurant-service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public RestaurantSnapshot getRestaurant(UUID restaurantId) {
        log.debug("action=get_restaurant restaurantId={}", restaurantId);
        return restClient.get()
                .uri("/restaurants/{id}", restaurantId)
                .retrieve()
                .body(RestaurantSnapshot.class);
    }

    @Override
    public List<MenuItemSnapshot> getMenuItems(UUID restaurantId, List<UUID> itemIds) {
        log.debug("action=get_menu_items restaurantId={} count={}", restaurantId, itemIds.size());
        Set<UUID> requested = Set.copyOf(itemIds);
        MenuClientResponse menu = restClient.get()
                .uri("/restaurants/{id}/menu", restaurantId)
                .retrieve()
                .body(MenuClientResponse.class);

        if (menu == null || menu.categories() == null) {
            return List.of();
        }
        return menu.categories().stream()
                .flatMap(cat -> cat.items() != null ? cat.items().stream() : java.util.stream.Stream.empty())
                .filter(item -> requested.contains(item.id()))
                .toList();
    }
}
