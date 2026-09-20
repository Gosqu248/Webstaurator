package com.gosqu.restaurant.menu.event;

import java.time.Instant;
import java.util.UUID;

public record MenuItemDeletedEvent(
        String eventId,
        UUID itemId,
        UUID restaurantId,
        Instant occurredAt
) {
}
