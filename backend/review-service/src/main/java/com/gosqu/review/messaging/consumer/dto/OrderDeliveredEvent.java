package com.gosqu.review.messaging.consumer.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Own (anti-corruption layer) copy of order-service's {@code OrderDeliveredEvent} —
 * field names must match the JSON published on the {@code order.delivered} topic.
 */
public record OrderDeliveredEvent(String eventId, UUID orderId, UUID customerId, UUID restaurantId, Instant occurredAt) {
}
