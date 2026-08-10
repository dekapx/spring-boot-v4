package com.dekapx.apps.service;

import com.dekapx.apps.model.Order;
import com.dekapx.apps.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumberIgnoreCase(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
    }

    @Transactional(readOnly = true)
    public String getOrderStatus(String orderNumber) {
        return getOrderByNumber(orderNumber).getStatus();
    }

    @Transactional
    public Order changeDeliveryLocation(String orderNumber, String newAddress) {
        Order order = getOrderByNumber(orderNumber);
        String status = order.getStatus() == null ? "" : order.getStatus().toUpperCase();
        if (status.equals("DELIVERED") || status.equals("CANCELLED")) {
            throw new IllegalStateException(
                    "Cannot change delivery location for order " + orderNumber +
                            " because its status is " + order.getStatus());
        }
        order.setDeliveryAddress(newAddress);
        return orderRepository.save(order);
    }
}
