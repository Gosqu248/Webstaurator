package com.gosqu.restaurant.restaurant.event;

import java.util.UUID;

public record RatingUpdatedEvent(UUID restaurantId, Double newAvgRating) {
}
