package com.gosqu.order.application.port.out;

import com.gosqu.order.domain.model.Order;

public interface OrderEventPublisherPort {

    void publishOrderCreated(Order order);

    void publishOrderConfirmed(Order order);

    void publishOrderPrepared(Order order);

    void publishOrderPickedUp(Order order);

    void publishOrderDelivered(Order order);

    void publishOrderCancelled(Order order);
}
