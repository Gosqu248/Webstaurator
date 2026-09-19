package com.gosqu.restaurant.restaurant;

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
        kafkaTemplate.send("restaurant.created", event.restaurantId().toString(), event);
    }

    public void publishUpdated(RestaurantUpdatedEvent event) {
        kafkaTemplate.send("restaurant.updated", event.restaurantId().toString(), event);
    }

    public void publishDeactivated(RestaurantDeactivatedEvent event) {
        kafkaTemplate.send("restaurant.deactivated", event.restaurantId().toString(), event);
    }
}
