package com.gosqu.review.common.exception;

import java.util.UUID;

public class RestaurantServiceUnavailableException extends RuntimeException {

    public RestaurantServiceUnavailableException(UUID restaurantId, Throwable cause) {
        super("Restaurant service unavailable for restaurantId=" + restaurantId, cause);
    }
}
