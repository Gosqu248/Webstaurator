package com.gosqu.review.review;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "reviews")
public record Review(
        @Id String id,
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        int restaurantRating,
        String restaurantComment,
        List<ReviewItem> items,
        ReviewReply reply,
        Instant createdAt,
        boolean visible
) {

    public static Review create(UUID orderId, UUID customerId, UUID restaurantId,
                                 int restaurantRating, String restaurantComment, List<ReviewItem> items) {
        return new Review(null, orderId, customerId, restaurantId, restaurantRating,
                restaurantComment, List.copyOf(items), null, Instant.now(), true);
    }

    public Review withReply(ReviewReply reply) {
        return new Review(id, orderId, customerId, restaurantId, restaurantRating,
                restaurantComment, items, reply, createdAt, visible);
    }

    public Review hidden() {
        return new Review(id, orderId, customerId, restaurantId, restaurantRating,
                restaurantComment, items, reply, createdAt, false);
    }
}
