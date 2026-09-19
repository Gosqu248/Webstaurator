package com.gosqu.payment.messaging.producer;

import com.gosqu.payment.event.PaymentCompletedEvent;
import com.gosqu.payment.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        send(KafkaTopics.PAYMENT_COMPLETED, event.orderId().toString(), event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        send(KafkaTopics.PAYMENT_FAILED, event.orderId().toString(), event);
    }

    private void send(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("action=publish_event_failed topic={} key={}", topic, key, ex);
                    } else {
                        log.debug("action=publish_event_ok topic={} key={}", topic, key);
                    }
                });
    }
}
