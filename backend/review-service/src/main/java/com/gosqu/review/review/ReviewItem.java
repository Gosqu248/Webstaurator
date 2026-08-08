package com.gosqu.review.review;

import java.util.UUID;

public record ReviewItem(UUID menuItemId, String name, int rating, String comment) {
}
