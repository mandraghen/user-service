package com.smorabito.customer.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;

@Testcontainers
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class CustomerServiceApplicationTests extends TestContainers {

    @Test
    void contextLoads() {
        // This test is used to check if the application context loads successfully
        // You can add more specific tests here as needed
    }
}
