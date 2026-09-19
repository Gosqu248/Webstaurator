package com.gosqu.order.application.port.in;

import com.gosqu.order.application.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface GetOrderUseCase {

    OrderResponse getOrder(UUID orderId, UUID requestingUserId);

    List<OrderResponse> getOrdersForCustomer(UUID customerId);

    List<OrderResponse> getOrdersForRestaurant(UUID restaurantId);
}
