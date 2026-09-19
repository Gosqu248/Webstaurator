package com.gosqu.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderDeliveredEvent(String eventId, UUID orderId, UUID customerId, UUID restaurantId, Instant occurredAt) {

    public static OrderDeliveredEvent of(UUID orderId, UUID customerId, UUID restaurantId) {
        return new OrderDeliveredEvent(UUID.randomUUID().toString(), orderId, customerId, restaurantId, Instant.now());
    }
}
