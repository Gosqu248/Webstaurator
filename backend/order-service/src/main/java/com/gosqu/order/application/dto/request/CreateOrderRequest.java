package com.gosqu.order.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID restaurantId,
        @NotEmpty @Valid List<OrderItemRequest> items) {

    public record OrderItemRequest(
            @NotNull UUID menuItemId,
            @Min(1) int quantity) {}
}
