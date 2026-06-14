package com.gosqu.restaurant.restaurant.event;

import java.time.Instant;
import java.util.UUID;

public record RestaurantUpdatedEvent(
        String eventId,
        UUID restaurantId,
        UUID ownerId,
        String name,
        String city,
        String cuisineType,
        Instant occurredAt
) {
}
