package com.gosqu.restaurant.search.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gosqu.restaurant.common.messaging.KafkaTopics;
import com.gosqu.restaurant.search.RestaurantSearchIndexer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Nasłuchuje eventów restauracji i dań z menu, i za każdym razem przelicza cały
 * RestaurantDocument od zera (patrz RestaurantSearchIndexer). Osobny groupId
 * ("restaurant-service-search-indexer") niż RatingUpdatedListener ("restaurant-service")
 * — inny strumień przetwarzania, więc inna consumer group, żeby nie mieszać offsetów
 * i rebalancingu między dwoma niezależnymi konsumentami tego serwisu.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantIndexConsumer {

    private static final String GROUP_ID = "restaurant-service-search-indexer";

    private final RestaurantSearchIndexer indexer;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {
            KafkaTopics.RESTAURANT_CREATED,
            KafkaTopics.RESTAURANT_UPDATED,
            KafkaTopics.RESTAURANT_DEACTIVATED,
            KafkaTopics.MENU_ITEM_CREATED,
            KafkaTopics.MENU_ITEM_UPDATED,
            KafkaTopics.MENU_ITEM_DELETED
    }, groupId = GROUP_ID)
    public void onRestaurantOrMenuChanged(String message) {
        try {
            RestaurantIdPayload payload = objectMapper.readValue(message, RestaurantIdPayload.class);
            indexer.reindexRestaurant(payload.restaurantId());
        } catch (Exception e) {
            log.error("restaurant_index_event_failed message={}", message, e);
        }
    }

    // Wszystkie 6 kształtów eventów mają pole restaurantId — ignoreUnknown pozwala
    // wyciągnąć tylko to jedno pole z dowolnego z nich, bez osobnego DTO na event.
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record RestaurantIdPayload(UUID restaurantId) {
    }
}
