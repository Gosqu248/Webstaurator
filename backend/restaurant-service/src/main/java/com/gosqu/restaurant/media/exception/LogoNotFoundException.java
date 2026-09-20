package com.gosqu.restaurant.media.exception;

import java.util.UUID;

public class LogoNotFoundException extends RuntimeException {
    public LogoNotFoundException(UUID restaurantId) {
        super("Restaurant has no logo: restaurantId=" + restaurantId);
    }
}
