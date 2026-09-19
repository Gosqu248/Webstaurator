package com.gosqu.payment.messaging.consumer;

import com.gosqu.payment.messaging.consumer.dto.OrderCancelledEvent;
import com.gosqu.payment.messaging.consumer.dto.OrderCreatedEvent;
import com.gosqu.payment.messaging.producer.KafkaTopics;
import com.gosqu.payment.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("action=order_created_received orderId={}", event.orderId());
        try {
            paymentService.createPendingPayment(event.orderId(), event.customerId(), event.totalAmount());
        } catch (Exception ex) {
            log.error("action=create_pending_payment_failed orderId={}", event.orderId(), ex);
        }
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CANCELLED, groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("action=order_cancelled_received orderId={}", event.orderId());
        try {
            paymentService.handleOrderCancelled(event.orderId());
        } catch (Exception ex) {
            log.error("action=handle_order_cancelled_failed orderId={}", event.orderId(), ex);
        }
    }
}
