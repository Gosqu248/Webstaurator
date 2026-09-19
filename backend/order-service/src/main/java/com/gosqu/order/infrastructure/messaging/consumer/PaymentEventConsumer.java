package com.gosqu.order.infrastructure.messaging.consumer;

import com.gosqu.order.application.port.in.UpdateOrderStatusUseCase;
import com.gosqu.order.domain.exception.InvalidOrderTransitionException;
import com.gosqu.order.domain.exception.OrderNotFoundException;
import com.gosqu.order.infrastructure.messaging.consumer.dto.PaymentCompletedEvent;
import com.gosqu.order.infrastructure.messaging.consumer.dto.PaymentFailedEvent;
import com.gosqu.order.infrastructure.messaging.producer.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final UpdateOrderStatusUseCase updateOrderStatus;

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED, groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("action=payment_completed_received orderId={}", event.orderId());
        try {
            updateOrderStatus.confirmOrder(event.orderId());
        } catch (OrderNotFoundException _) {
            log.error("action=confirm_order_failed reason=order_not_found orderId={}", event.orderId());
        } catch (InvalidOrderTransitionException ex) {
            log.warn("action=confirm_order_skipped orderId={} reason={}", event.orderId(), ex.getMessage());
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("action=payment_failed_received orderId={}", event.orderId());
        try {
            updateOrderStatus.failPayment(event.orderId());
        } catch (OrderNotFoundException _) {
            log.error("action=fail_payment_failed reason=order_not_found orderId={}", event.orderId());
        } catch (InvalidOrderTransitionException ex) {
            log.warn("action=fail_payment_skipped orderId={} reason={}", event.orderId(), ex.getMessage());
        }
    }
}
