package com.gosqu.delivery.tracking.exception;

import java.util.UUID;

public class DeliveryTrackingNotFoundException extends RuntimeException {
    public DeliveryTrackingNotFoundException(UUID orderId) {
        super("Delivery tracking not found for orderId=" + orderId);
    }
}
