package com.gosqu.order.domain.exception;

import java.util.UUID;

public class ForbiddenOrderAccessException extends RuntimeException {

    public ForbiddenOrderAccessException(UUID orderId, UUID userId) {
        super("User " + userId + " is not allowed to access order " + orderId);
    }
}
