package com.gosqu.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelledEvent(String eventId, UUID orderId, UUID customerId,
                                   String reason, Instant occurredAt) {

    public static OrderCancelledEvent of(UUID orderId, UUID customerId, String reason) {
        return new OrderCancelledEvent(UUID.randomUUID().toString(), orderId, customerId,
                reason, Instant.now());
    }
}
