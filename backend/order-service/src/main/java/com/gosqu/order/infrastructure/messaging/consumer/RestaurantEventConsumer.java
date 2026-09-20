package com.gosqu.order.infrastructure.messaging.consumer;

import com.gosqu.order.infrastructure.cache.RestaurantAvailabilityCache;
import com.gosqu.order.infrastructure.messaging.consumer.dto.RestaurantDeactivatedEvent;
import com.gosqu.order.infrastructure.messaging.producer.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantEventConsumer {

    private final RestaurantAvailabilityCache restaurantAvailabilityCache;

    @KafkaListener(topics = KafkaTopics.RESTAURANT_DEACTIVATED, groupId = "${spring.kafka.consumer.group-id}")
    public void onDeactivated(RestaurantDeactivatedEvent event) {
        log.info("action=restaurant_deactivated_received restaurantId={}", event.restaurantId());
        restaurantAvailabilityCache.markUnavailable(event.restaurantId());
    }
}
