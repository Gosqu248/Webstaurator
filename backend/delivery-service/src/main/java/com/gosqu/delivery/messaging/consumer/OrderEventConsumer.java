package com.gosqu.delivery.messaging.consumer;

import com.gosqu.delivery.messaging.KafkaTopics;
import com.gosqu.delivery.messaging.consumer.dto.OrderConfirmedEvent;
import com.gosqu.delivery.messaging.consumer.dto.OrderPreparedEvent;
import com.gosqu.delivery.tracking.DeliveryTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final DeliveryTrackingService deliveryTrackingService;

    @KafkaListener(topics = KafkaTopics.ORDER_CONFIRMED, groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        deliveryTrackingService.assignForOrder(event.orderId(), event.customerId());
    }

    @KafkaListener(topics = KafkaTopics.ORDER_PREPARED, groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderPrepared(OrderPreparedEvent event) {
        deliveryTrackingService.markAtRestaurant(event.orderId());
    }
}
