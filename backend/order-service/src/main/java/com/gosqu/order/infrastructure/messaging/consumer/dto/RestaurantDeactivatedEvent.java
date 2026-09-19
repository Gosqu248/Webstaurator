package com.gosqu.order.infrastructure.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

public record RestaurantDeactivatedEvent(
        String eventId,
        UUID restaurantId,
        UUID ownerId,
        Instant occurredAt) {
}
