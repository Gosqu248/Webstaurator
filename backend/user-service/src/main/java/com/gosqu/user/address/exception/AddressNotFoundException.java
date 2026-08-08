package com.gosqu.user.address.exception;

import java.util.UUID;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(UUID id) {
        super("Address not found: id=" + id);
    }

    public AddressNotFoundException(UUID userId, boolean defaultAddress) {
        super("No default address for userId=" + userId);
    }
}
