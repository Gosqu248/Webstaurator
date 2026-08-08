package com.gosqu.order.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID menuItemId,
        String name,
        BigDecimal unitPrice,
        String currency,
        int quantity,
        BigDecimal subtotal) {
}
