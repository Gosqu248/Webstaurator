package com.gosqu.notificationservice.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        String eventId,
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        String reason,
        Instant occurredAt) {
}
