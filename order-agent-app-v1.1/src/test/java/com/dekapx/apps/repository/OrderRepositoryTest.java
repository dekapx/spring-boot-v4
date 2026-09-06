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
    public static final String ORDER_NUMBER = "ORD-1001";
    public static final String TRACKING_NUMBER = "FX123456789US";

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
        Optional<Order> order = this.orderRepository.findByOrderNumberIgnoreCase(ORDER_NUMBER);
        assertThat(order)
                .isNotNull()
                .satisfies(o ->
                {
                    assertThat(o.get().getOrderNumber()).isEqualTo(ORDER_NUMBER);
                    assertThat(o.get().getCustomerName()).isEqualTo("John Smith");
                    assertThat(o.get().getItemName()).isEqualTo("Wireless Mouse");
                });
    }

    @Test
    public void shouldReturnOrderForGivenTrackingNumber() {
        Optional<Order> order = this.orderRepository.findByTrackingNumberIgnoreCase(TRACKING_NUMBER);
        assertThat(order)
                .isNotNull()
                .satisfies(o ->
                {
                    assertThat(o.get().getTrackingNumber()).isEqualTo(TRACKING_NUMBER);
                    assertThat(o.get().getCustomerName()).isEqualTo("John Smith");
                    assertThat(o.get().getItemName()).isEqualTo("Wireless Mouse");
                });
    }
}
