package com.gosqu.review.messaging.producer;

import com.gosqu.review.messaging.KafkaTopics;
import com.gosqu.review.messaging.producer.dto.RatingUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishRatingUpdated(UUID restaurantId, double newAvgRating) {
        var event = new RatingUpdatedEvent(restaurantId, newAvgRating);
        kafkaTemplate.send(KafkaTopics.REVIEW_RESTAURANT_RATED, restaurantId.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("action=publish_rating_updated_failed restaurantId={}", restaurantId, ex);
                    } else {
                        log.debug("action=publish_rating_updated_ok restaurantId={} rating={}", restaurantId, newAvgRating);
                    }
                });
    }
}
