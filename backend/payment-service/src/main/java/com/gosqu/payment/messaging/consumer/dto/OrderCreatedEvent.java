package com.gosqu.payment.messaging.consumer.dto;

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
}
