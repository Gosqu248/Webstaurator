package com.gosqu.order.domain.model;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    PREPARED,
    PICKED_UP,
    DELIVERED,
    CANCELLED,
    PAYMENT_FAILED
}
