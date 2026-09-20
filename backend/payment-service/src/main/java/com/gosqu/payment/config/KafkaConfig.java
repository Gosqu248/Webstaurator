package com.gosqu.payment.config;

import com.gosqu.payment.messaging.producer.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_COMPLETED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_FAILED).partitions(3).replicas(1).build();
    }

    // Dead-letter topiki dla konsumentów tego serwisu (Część 3.1)
    @Bean
    public NewTopic orderCreatedDltTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_CREATED + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderCancelledDltTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_CANCELLED + ".DLT").partitions(3).replicas(1).build();
    }
}
