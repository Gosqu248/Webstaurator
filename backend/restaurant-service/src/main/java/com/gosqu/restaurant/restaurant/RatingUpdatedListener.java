package com.gosqu.restaurant.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gosqu.restaurant.restaurant.event.RatingUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatingUpdatedListener {

    private final RestaurantService restaurantService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "review.restaurant-rated", groupId = "restaurant-service")
    public void onRatingUpdated(String message) {
        try {
            RatingUpdatedEvent event = objectMapper.readValue(message, RatingUpdatedEvent.class);
            restaurantService.updateAvgRating(event.restaurantId(), event.newAvgRating());
            log.info("rating_updated restaurantId={} newRating={}", event.restaurantId(), event.newAvgRating());

        } catch (Exception e) {
            log.error("rating_update_failed message={}", message, e);
        }
    }
}
