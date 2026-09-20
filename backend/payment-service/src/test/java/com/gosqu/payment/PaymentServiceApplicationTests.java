package com.gosqu.payment;

import com.gosqu.payment.config.TestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = TestContainerConfig.class)
class PaymentServiceApplicationTests {

    @Container
    @ServiceConnection
    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.7.1");

    @Test
    void contextLoads() {
    }

}
