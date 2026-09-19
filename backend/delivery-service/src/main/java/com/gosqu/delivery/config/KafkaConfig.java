package com.gosqu.delivery.config;

import com.gosqu.delivery.messaging.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // Dead-letter topiki dla konsumentów tego serwisu (Część 3.1)
    @Bean
    public NewTopic orderConfirmedDltTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_CONFIRMED + ".DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderPreparedDltTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_PREPARED + ".DLT").partitions(3).replicas(1).build();
    }
}
