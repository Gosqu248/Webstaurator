package com.gosqu.review.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String id) {
        super("Review not found: id=" + id);
    }
}
