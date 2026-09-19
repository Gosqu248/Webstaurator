package com.gosqu.order.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemSnapshot(
        UUID id,
        String name,
        BigDecimal price,
        Boolean isAvailable) {
}
