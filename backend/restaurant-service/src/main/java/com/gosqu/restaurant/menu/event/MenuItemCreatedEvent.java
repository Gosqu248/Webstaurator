package com.gosqu.restaurant.menu.event;

import java.time.Instant;
import java.util.UUID;

public record MenuItemCreatedEvent(
        String eventId,
        UUID itemId,
        UUID restaurantId,
        String name,
        Instant occurredAt
) {
}
