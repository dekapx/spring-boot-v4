package com.dekapx.apps.repository;

import com.dekapx.apps.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        Optional<Order> order = this.orderRepository.findByOrderNumber("ORD-1001");
        assertThat(order)
                .isNotNull()
                .satisfies(o ->
                {
                    assertThat(o.get().getOrderNumber()).isEqualTo("ORD-1001");
                    assertThat(o.get().getCustomerName()).isEqualTo("Alice Johnson");
                    assertThat(o.get().getItemName()).isEqualTo("Wireless Headphones");
                });
    }
}
