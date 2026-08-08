package com.gosqu.order.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        String eventId,
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        BigDecimal totalAmount,
        String currency,
        Instant occurredAt) {

    public static OrderCreatedEvent of(UUID orderId, UUID customerId, UUID restaurantId,
                                       BigDecimal totalAmount, String currency) {
        return new OrderCreatedEvent(UUID.randomUUID().toString(), orderId, customerId,
                restaurantId, totalAmount, currency, Instant.now());
    }
}
