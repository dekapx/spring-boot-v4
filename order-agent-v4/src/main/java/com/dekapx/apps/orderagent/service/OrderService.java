package com.dekapx.apps.orderagent.service;

import com.dekapx.apps.orderagent.dto.OrderDtos.OrderCreateRequest;
import com.dekapx.apps.orderagent.dto.OrderDtos.OrderResponse;
import com.dekapx.apps.orderagent.entity.Order;
import com.dekapx.apps.orderagent.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        Order saved = orderRepository.save(orderMapper.toEntity(request));
        return orderMapper.toResponse(saved);
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    public OrderResponse findByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumberIgnoreCase(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        return orderMapper.toResponse(order);
    }

    public String findStatus(String orderNumber) {
        Order order = orderRepository.findByOrderNumberIgnoreCase(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        return order.getStatus();
    }

    public List<OrderResponse> findByCustomerName(String customerName) {
        return orderRepository.findByCustomerNameIgnoreCaseContaining(customerName)
                .stream().map(orderMapper::toResponse).toList();
    }

    @Transactional
    public OrderResponse changeDeliveryLocation(String orderNumber, String newAddress) {
        Order order = orderRepository.findByOrderNumberIgnoreCase(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));

        if ("DELIVERED".equalsIgnoreCase(order.getStatus()) || "CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException(
                    "Cannot change delivery location for an order that is already " + order.getStatus());
        }

        order.setDeliveryAddress(newAddress);
        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }
}
