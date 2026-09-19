package com.gosqu.order.domain.model;

import java.util.UUID;

public record OrderItem(UUID id, UUID menuItemId, String name, Money unitPrice, int quantity) {

    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }
}
