package com.dekapx.apps.repository;

import com.dekapx.apps.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumberIgnoreCase(String orderNumber);

    Optional<Order> findByTrackingNumberIgnoreCase(String trackingNumber);
}
