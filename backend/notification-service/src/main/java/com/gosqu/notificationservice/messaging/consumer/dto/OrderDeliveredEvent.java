package com.gosqu.notificationservice.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderDeliveredEvent(String eventId, UUID orderId, UUID customerId, UUID restaurantId, Instant occurredAt) {
}
