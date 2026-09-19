package com.gosqu.notificationservice.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderPickedUpEvent(String eventId, UUID orderId, UUID customerId, Instant occurredAt) {
}
