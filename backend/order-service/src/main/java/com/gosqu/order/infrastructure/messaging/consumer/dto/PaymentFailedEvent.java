package com.gosqu.order.infrastructure.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        String eventId,
        UUID orderId,
        UUID customerId,
        String reason,
        Instant occurredAt) {
}
