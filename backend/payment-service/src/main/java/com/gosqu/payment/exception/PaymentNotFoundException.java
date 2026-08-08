package com.gosqu.payment.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(UUID orderId) {
        super("Payment not found for orderId=" + orderId);
    }
}
