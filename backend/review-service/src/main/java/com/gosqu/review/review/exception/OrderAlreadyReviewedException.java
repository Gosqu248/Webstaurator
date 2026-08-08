package com.gosqu.review.review.exception;

import java.util.UUID;

public class OrderAlreadyReviewedException extends RuntimeException {
    public OrderAlreadyReviewedException(UUID orderId) {
        super("Order has already been reviewed: orderId=" + orderId);
    }
}
