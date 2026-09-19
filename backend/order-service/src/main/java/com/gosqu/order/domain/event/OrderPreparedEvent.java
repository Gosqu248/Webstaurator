package com.gosqu.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderPreparedEvent(String eventId, UUID orderId, UUID restaurantId, UUID customerId, Instant occurredAt) {

    public static OrderPreparedEvent of(UUID orderId, UUID restaurantId, UUID customerId) {
        return new OrderPreparedEvent(UUID.randomUUID().toString(), orderId, restaurantId, customerId, Instant.now());
    }
}
