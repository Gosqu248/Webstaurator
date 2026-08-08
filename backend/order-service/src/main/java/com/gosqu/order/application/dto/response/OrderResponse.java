package com.gosqu.order.application.dto.response;

import com.gosqu.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID customerId,
        UUID restaurantId,
        String restaurantName,
        List<OrderItemResponse> items,
        DeliveryAddressResponse deliveryAddress,
        BigDecimal deliveryFee,
        BigDecimal totalAmount,
        String currency,
        OrderStatus status,
        String cancellationReason,
        Instant createdAt,
        Instant updatedAt) {

    public record DeliveryAddressResponse(
            String street,
            String city,
            String postalCode,
            String country,
            Double latitude,
            Double longitude) {}
}
