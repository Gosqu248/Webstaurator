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
}
