package com.gosqu.review.messaging.consumer;

import com.gosqu.review.eligibility.ReviewableOrder;
import com.gosqu.review.eligibility.ReviewableOrderRepository;
import com.gosqu.review.messaging.KafkaTopics;
import com.gosqu.review.messaging.consumer.dto.OrderDeliveredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliveredConsumer {

    private final ReviewableOrderRepository reviewableOrderRepository;

    @KafkaListener(topics = KafkaTopics.ORDER_DELIVERED, groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderDelivered(OrderDeliveredEvent event) {
        if (reviewableOrderRepository.findByOrderId(event.orderId()).isPresent()) {
            log.debug("action=order_already_reviewable orderId={}", event.orderId());
            return;
        }
        ReviewableOrder reviewableOrder = ReviewableOrder.create(
                event.orderId(), event.customerId(), event.restaurantId());
        reviewableOrderRepository.save(reviewableOrder);
        log.info("action=order_marked_reviewable orderId={} restaurantId={}", event.orderId(), event.restaurantId());
    }
}
