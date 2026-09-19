package com.gosqu.order.domain.exception;

import java.util.UUID;

public class UserServiceUnavailableException extends RuntimeException {

    public UserServiceUnavailableException(UUID userId, Throwable cause) {
        super("User service unavailable for userId=" + userId, cause);
    }
}
