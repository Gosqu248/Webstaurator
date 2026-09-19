package com.gosqu.review.review.exception;

import java.util.UUID;

public class OrderNotEligibleForReviewException extends RuntimeException {
    public OrderNotEligibleForReviewException(UUID orderId) {
        super("Order is not eligible for review (not delivered yet or does not exist): orderId=" + orderId);
    }
}
