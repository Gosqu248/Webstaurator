package com.gosqu.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Serwis czysto konsumencki — nie tworzy topików źródłowych (robią to ich producenci:
 * order-service, payment-service). Tworzy natomiast dead-letter topiki dla własnych
 * konsumentów (Część 3.1), bo za nie odpowiada wyłącznie ten serwis.
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderCreatedDltTopic() {
        return TopicBuilder.name("order.created.DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderConfirmedDltTopic() {
        return TopicBuilder.name("order.confirmed.DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderPreparedDltTopic() {
        return TopicBuilder.name("order.prepared.DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderPickedUpDltTopic() {
        return TopicBuilder.name("order.picked-up.DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderDeliveredDltTopic() {
        return TopicBuilder.name("order.delivered.DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderCancelledDltTopic() {
        return TopicBuilder.name("order.cancelled.DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFailedDltTopic() {
        return TopicBuilder.name("payment.failed.DLT").partitions(3).replicas(1).build();
    }
}
