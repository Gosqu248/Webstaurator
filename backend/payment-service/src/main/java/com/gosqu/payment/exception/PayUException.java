package com.gosqu.payment.exception;

public class PayUException extends RuntimeException {

    public PayUException(String message) {
        super(message);
    }

    public PayUException(String message, Throwable cause) {
        super(message, cause);
    }
}
