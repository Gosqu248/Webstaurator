package com.gosqu.order.infrastructure.messaging.consumer.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(
        String eventId,
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        Instant occurredAt) {
}
