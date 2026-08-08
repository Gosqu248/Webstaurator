package com.gosqu.order.domain.exception;

import com.gosqu.order.domain.model.OrderStatus;

import java.util.UUID;

public class InvalidOrderTransitionException extends RuntimeException {

    public InvalidOrderTransitionException(UUID orderId, OrderStatus current, OrderStatus target) {
        super("Cannot transition order " + orderId + " from " + current + " to " + target);
    }
}
