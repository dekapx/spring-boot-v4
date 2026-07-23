package com.dekapx.apps.service;

import com.dekapx.apps.model.Order;
import com.dekapx.apps.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumberIgnoreCase(orderNumber);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> updateOrder(Long id, Order updated) {
        return orderRepository.findById(id).map(existing -> {
            existing.setOrderNumber(updated.getOrderNumber());
            existing.setCustomerName(updated.getCustomerName());
            existing.setItemName(updated.getItemName());
            existing.setQuantity(updated.getQuantity());
            existing.setTotalAmount(updated.getTotalAmount());
            existing.setStatus(updated.getStatus());
            existing.setOrderDate(updated.getOrderDate());
            existing.setEstimatedDeliveryDate(updated.getEstimatedDeliveryDate());
            existing.setTrackingNumber(updated.getTrackingNumber());
            existing.setCarrier(updated.getCarrier());
            existing.setCurrentLocation(updated.getCurrentLocation());
            return orderRepository.save(existing);
        });
    }

    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
