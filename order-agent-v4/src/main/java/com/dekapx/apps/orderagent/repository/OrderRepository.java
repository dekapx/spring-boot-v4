package com.dekapx.apps.orderagent.repository;

import com.dekapx.apps.orderagent.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumberIgnoreCase(String orderNumber);

    List<Order> findByCustomerNameIgnoreCaseContaining(String customerName);

    List<Order> findByStatusIgnoreCase(String status);

    boolean existsByOrderNumberIgnoreCase(String orderNumber);
}
