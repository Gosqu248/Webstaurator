package com.gosqu.user.address.exception;

public class AddressLimitExceededException extends RuntimeException {
    public AddressLimitExceededException(int limit) {
        super("Address limit reached: maximum " + limit + " addresses per user");
    }
}
