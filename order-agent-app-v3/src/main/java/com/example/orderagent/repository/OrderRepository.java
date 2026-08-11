package com.example.orderagent.repository;

import com.example.orderagent.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumberIgnoreCase(String orderNumber);

    Optional<Order> findByTrackingNumberIgnoreCase(String trackingNumber);
}
