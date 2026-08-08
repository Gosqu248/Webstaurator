package com.gosqu.review.review.dto.response;

import com.gosqu.review.review.ReviewReply;

import java.time.Instant;
import java.util.UUID;

public record ReviewReplyResponse(UUID ownerId, String message, Instant respondedAt) {

    public static ReviewReplyResponse from(ReviewReply reply) {
        return new ReviewReplyResponse(reply.ownerId(), reply.message(), reply.respondedAt());
    }
}
