package com.gosqu.restaurant.restaurant;

import com.gosqu.restaurant.common.messaging.KafkaTopics;
import com.gosqu.restaurant.restaurant.event.RestaurantCreatedEvent;
import com.gosqu.restaurant.restaurant.event.RestaurantDeactivatedEvent;
import com.gosqu.restaurant.restaurant.event.RestaurantUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCreated(RestaurantCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_CREATED, event.restaurantId().toString(), event);
    }

    public void publishUpdated(RestaurantUpdatedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_UPDATED, event.restaurantId().toString(), event);
    }

    public void publishDeactivated(RestaurantDeactivatedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_DEACTIVATED, event.restaurantId().toString(), event);
    }
}
