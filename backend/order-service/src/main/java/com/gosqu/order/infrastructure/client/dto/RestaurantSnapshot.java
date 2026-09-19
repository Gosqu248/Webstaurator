package com.gosqu.order.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantSnapshot(
        UUID id,
        String name,
        String city,
        Boolean isActive,
        BigDecimal deliveryFee) {
}
