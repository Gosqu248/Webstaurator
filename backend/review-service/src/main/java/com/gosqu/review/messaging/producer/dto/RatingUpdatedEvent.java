package com.gosqu.review.messaging.producer.dto;

import java.util.UUID;

/**
 * Consumed by restaurant-service's {@code RatingUpdatedListener} on the
 * {@code review.restaurant-rated} topic — field names must stay in sync with it.
 */
public record RatingUpdatedEvent(UUID restaurantId, Double newAvgRating) {
}
