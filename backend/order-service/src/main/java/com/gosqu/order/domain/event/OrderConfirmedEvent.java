package com.gosqu.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(String eventId, UUID orderId, UUID customerId, Instant occurredAt) {

    public static OrderConfirmedEvent of(UUID orderId, UUID customerId) {
        return new OrderConfirmedEvent(UUID.randomUUID().toString(), orderId, customerId, Instant.now());
    }
}
