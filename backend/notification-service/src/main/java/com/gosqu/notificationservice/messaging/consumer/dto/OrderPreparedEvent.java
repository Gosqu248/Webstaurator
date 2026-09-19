package com.gosqu.notificationservice.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderPreparedEvent(String eventId, UUID orderId, UUID restaurantId, UUID customerId, Instant occurredAt) {
}
