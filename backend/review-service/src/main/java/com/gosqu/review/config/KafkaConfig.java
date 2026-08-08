package com.gosqu.review.config;

import com.gosqu.review.messaging.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic reviewRestaurantRatedTopic() {
        return TopicBuilder.name(KafkaTopics.REVIEW_RESTAURANT_RATED).partitions(3).replicas(1).build();
    }
}
