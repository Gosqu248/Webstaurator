package com.gosqu.review.client;

import com.gosqu.review.client.dto.RestaurantOwnerResponse;
import com.gosqu.review.common.exception.RestaurantServiceUnavailableException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
public class RestaurantClient {

    private static final String INTERNAL_HEADER = "X-Internal-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    RestaurantClient(RestClient.Builder restClientBuilder,
                      @Value("${services.restaurant-service.url}") String baseUrl,
                      @Value("${internal.api-key}") String internalApiKey) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.internalApiKey = internalApiKey;
    }

    @CircuitBreaker(name = "restaurant-service")
    @Bulkhead(name = "restaurant-service")
    @Retry(name = "restaurant-service", fallbackMethod = "getOwnerIdFallback")
    public UUID getOwnerId(UUID restaurantId) {
        log.debug("action=get_restaurant_owner restaurantId={}", restaurantId);
        RestaurantOwnerResponse response = restClient.get()
                .uri("/internal/restaurants/{id}/owner", restaurantId)
                .header(INTERNAL_HEADER, internalApiKey)
                .retrieve()
                .body(RestaurantOwnerResponse.class);
        return response != null ? response.ownerId() : null;
    }

    private UUID getOwnerIdFallback(UUID restaurantId, HttpClientErrorException ex) {
        throw ex;
    }

    private UUID getOwnerIdFallback(UUID restaurantId, Throwable ex) {
        log.warn("action=restaurant_service_unavailable restaurantId={} cause={}", restaurantId, ex.toString());
        throw new RestaurantServiceUnavailableException(restaurantId, ex);
    }
}
