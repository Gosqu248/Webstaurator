package com.gosqu.order.application.port.in;

import com.gosqu.order.application.dto.response.OrderResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface CreateOrderUseCase {

    OrderResponse createOrder(CreateOrderCommand command);

    record CreateOrderCommand(
            @NotNull UUID customerId,
            @NotNull UUID restaurantId,
            @NotEmpty List<OrderItemCommand> items) {}

    record OrderItemCommand(
            @NotNull UUID menuItemId,
            @Min(1) int quantity) {}
}
