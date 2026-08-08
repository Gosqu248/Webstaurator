package com.gosqu.review.client;

import com.gosqu.review.client.dto.RestaurantOwnerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
public class RestaurantClient {

    private static final String INTERNAL_HEADER = "X-Internal-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    RestaurantClient(@Value("${services.restaurant-service.url}") String baseUrl,
                      @Value("${internal.api-key}") String internalApiKey) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.internalApiKey = internalApiKey;
    }

    public UUID getOwnerId(UUID restaurantId) {
        log.debug("action=get_restaurant_owner restaurantId={}", restaurantId);
        RestaurantOwnerResponse response = restClient.get()
                .uri("/internal/restaurants/{id}/owner", restaurantId)
                .header(INTERNAL_HEADER, internalApiKey)
                .retrieve()
                .body(RestaurantOwnerResponse.class);
        return response != null ? response.ownerId() : null;
    }
}
