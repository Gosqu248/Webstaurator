package com.gosqu.notificationservice.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(String eventId, UUID orderId, UUID customerId, Instant occurredAt) {
}

