package com.gosqu.order.infrastructure.config;

import com.gosqu.order.infrastructure.messaging.producer.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_CREATED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_CONFIRMED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderPreparedTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_PREPARED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderPickedUpTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_PICKED_UP).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderDeliveredTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_DELIVERED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_CANCELLED).partitions(3).replicas(1).build();
    }

    // Dead-letter topiki dla konsumentów tego serwisu (Część 3.1) — ta sama liczba partycji
    // co temat źródłowy; DeadLetterPublishingRecoverer i tak wymusza wybór partycji na -1.
    @Bean
    public NewTopic paymentCompletedDltTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_COMPLETED + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFailedDltTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_FAILED + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic restaurantDeactivatedDltTopic() {
        return TopicBuilder.name(KafkaTopics.RESTAURANT_DEACTIVATED + ".DLT").partitions(3).replicas(1).build();
    }
}
