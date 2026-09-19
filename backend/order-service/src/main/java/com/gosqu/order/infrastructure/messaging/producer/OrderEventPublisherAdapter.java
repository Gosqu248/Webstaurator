package com.gosqu.order.infrastructure.messaging.producer;

import com.gosqu.order.application.port.out.OrderEventPublisherPort;
import com.gosqu.order.domain.event.*;
import com.gosqu.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisherAdapter implements OrderEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishOrderCreated(Order order) {
        var event = OrderCreatedEvent.of(order.getId(), order.getCustomerId(),
                order.getRestaurantId(), order.getTotalAmount().amount(),
                order.getTotalAmount().currency());
        send(KafkaTopics.ORDER_CREATED, order.getId().toString(), event);
    }

    @Override
    public void publishOrderConfirmed(Order order) {
        var event = OrderConfirmedEvent.of(order.getId(), order.getCustomerId());
        send(KafkaTopics.ORDER_CONFIRMED, order.getId().toString(), event);
    }

    @Override
    public void publishOrderPrepared(Order order) {
        var event = OrderPreparedEvent.of(order.getId(), order.getRestaurantId(), order.getCustomerId());
        send(KafkaTopics.ORDER_PREPARED, order.getId().toString(), event);
    }

    @Override
    public void publishOrderPickedUp(Order order) {
        var event = OrderPickedUpEvent.of(order.getId(), order.getCustomerId());
        send(KafkaTopics.ORDER_PICKED_UP, order.getId().toString(), event);
    }

    @Override
    public void publishOrderDelivered(Order order) {
        var event = OrderDeliveredEvent.of(order.getId(), order.getCustomerId(), order.getRestaurantId());
        send(KafkaTopics.ORDER_DELIVERED, order.getId().toString(), event);
    }

    @Override
    public void publishOrderCancelled(Order order) {
        var event = OrderCancelledEvent.of(order.getId(), order.getCustomerId(),
                order.getCancellationReason());
        send(KafkaTopics.ORDER_CANCELLED, order.getId().toString(), event);
    }

    private void send(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("action=publish_event_failed topic={} key={}", topic, key, ex);
                    } else {
                        log.debug("action=publish_event_ok topic={} key={}", topic, key);
                    }
                });
    }
}
