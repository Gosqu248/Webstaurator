package com.gosqu.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderPickedUpEvent(String eventId, UUID orderId, UUID customerId, Instant occurredAt) {

    public static OrderPickedUpEvent of(UUID orderId, UUID customerId) {
        return new OrderPickedUpEvent(UUID.randomUUID().toString(), orderId, customerId, Instant.now());
    }
}
