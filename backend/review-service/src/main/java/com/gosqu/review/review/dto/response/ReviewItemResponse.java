package com.gosqu.review.review.dto.response;

import com.gosqu.review.review.ReviewItem;

import java.util.UUID;

public record ReviewItemResponse(UUID menuItemId, String name, int rating, String comment) {

    public static ReviewItemResponse from(ReviewItem item) {
        return new ReviewItemResponse(item.menuItemId(), item.name(), item.rating(), item.comment());
    }
}
