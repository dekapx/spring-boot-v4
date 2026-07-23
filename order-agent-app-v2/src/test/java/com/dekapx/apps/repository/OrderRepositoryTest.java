package com.dekapx.apps.repository;

import com.dekapx.apps.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setup() {
    }

    @AfterEach
    public void tearDown() {
        this.orderRepository.deleteAll();
    }

    @Test
    public void shouldReturnOrderForGivenOrderNumber() {
        Optional<Order> sensorReadings = this.orderRepository.findByOrderNumber("ORD-1001");
        assertThat(sensorReadings)
                .isNotNull();
    }
}
