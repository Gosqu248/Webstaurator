package com.gosqu.auth.exception;

public class InvalidCodeException extends RuntimeException {
    public InvalidCodeException() {
        super("Invalid or expired verification code");
    }
}
