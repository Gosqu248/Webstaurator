package com.gosqu.delivery.messaging.producer;

import com.gosqu.delivery.messaging.KafkaTopics;
import com.gosqu.delivery.messaging.producer.dto.DeliveryLocationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryLocationPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(DeliveryLocationEvent event) {
        kafkaTemplate.send(KafkaTopics.DELIVERY_LOCATION, event.orderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("action=publish_location_failed orderId={}", event.orderId(), ex);
                    }
                });
    }
}
