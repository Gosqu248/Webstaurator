package com.gosqu.review.review.dto.response;

import com.gosqu.review.review.Review;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReviewResponse(
        String id,
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        int restaurantRating,
        String restaurantComment,
        List<ReviewItemResponse> items,
        ReviewReplyResponse reply,
        Instant createdAt
) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.id(),
                review.orderId(),
                review.customerId(),
                review.restaurantId(),
                review.restaurantRating(),
                review.restaurantComment(),
                review.items().stream().map(ReviewItemResponse::from).toList(),
                review.reply() != null ? ReviewReplyResponse.from(review.reply()) : null,
                review.createdAt()
        );
    }
}
