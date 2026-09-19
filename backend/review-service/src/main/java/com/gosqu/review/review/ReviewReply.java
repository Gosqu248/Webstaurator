package com.gosqu.review.review;

import java.time.Instant;
import java.util.UUID;

public record ReviewReply(UUID ownerId, String message, Instant respondedAt) {

    public static ReviewReply of(UUID ownerId, String message) {
        return new ReviewReply(ownerId, message, Instant.now());
    }
}
