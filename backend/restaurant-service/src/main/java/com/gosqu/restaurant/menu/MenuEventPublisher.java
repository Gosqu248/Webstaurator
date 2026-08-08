package com.gosqu.restaurant.menu;

import com.gosqu.restaurant.common.messaging.KafkaTopics;
import com.gosqu.restaurant.menu.event.MenuItemCreatedEvent;
import com.gosqu.restaurant.menu.event.MenuItemDeletedEvent;
import com.gosqu.restaurant.menu.event.MenuItemUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishItemCreated(MenuItemCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.MENU_ITEM_CREATED, event.restaurantId().toString(), event);
    }

    public void publishItemUpdated(MenuItemUpdatedEvent event) {
        kafkaTemplate.send(KafkaTopics.MENU_ITEM_UPDATED, event.restaurantId().toString(), event);
    }

    public void publishItemDeleted(MenuItemDeletedEvent event) {
        kafkaTemplate.send(KafkaTopics.MENU_ITEM_DELETED, event.restaurantId().toString(), event);
    }
}
