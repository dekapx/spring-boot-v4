package com.dekapx.apps.service;

import com.dekapx.apps.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAgentTools {
    private final OrderService orderService;

    @Tool(description = "Fetch order status details from the database by order number")
    public String findByOrderNumber(String orderNumber) {
        return orderService.getOrderByNumber(orderNumber)
                .map(this::buildToolResponse)
                .orElse("Order not found");
    }

    @Tool(description = "Fetch all orders from the database and return order numbers")
    public String loadAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return orders.stream()
                .map(Order::getOrderNumber)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("No orders found");
    }

    private String buildToolResponse(Order order) {
        return String.format("Order Number: %s, Status: %s, Estimated Delivery: %s",
                order.getOrderNumber(),
                order.getStatus(),
                order.getEstimatedDeliveryDate());
    }
}
