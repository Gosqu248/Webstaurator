package com.gosqu.order.application.port.in;

import java.util.UUID;

public interface UpdateOrderStatusUseCase {

    void confirmOrder(UUID orderId);

    void startPreparing(UUID orderId);

    void markPrepared(UUID orderId);

    void markPickedUp(UUID orderId);

    void markDelivered(UUID orderId);

    void cancelOrder(UUID orderId, UUID requestingUserId, String reason);

    void failPayment(UUID orderId);
}
