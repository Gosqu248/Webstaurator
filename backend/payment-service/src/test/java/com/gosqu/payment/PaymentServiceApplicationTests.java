package com.gosqu.payment;

import com.gosqu.payment.config.TestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = TestContainerConfig.class)
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
