package com.gosqu.notificationservice.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelledEvent(
        String eventId,
        UUID orderId,
        UUID customerId,
        String reason,
        Instant occurredAt
) {
}
