package com.gosqu.review.eligibility;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Tracks which delivered orders are eligible for a review. Created when {@code order.delivered}
 * is consumed from Kafka; consumed (marked reviewed) when the customer actually posts a review.
 */
@Document(collection = "reviewable_orders")
public record ReviewableOrder(
        @Id String id,
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        Instant deliveredAt,
        boolean reviewed
) {

    public static ReviewableOrder create(UUID orderId, UUID customerId, UUID restaurantId) {
        return new ReviewableOrder(orderId.toString(), orderId, customerId, restaurantId, Instant.now(), false);
    }

    public ReviewableOrder markReviewed() {
        return new ReviewableOrder(id, orderId, customerId, restaurantId, deliveredAt, true);
    }
}
