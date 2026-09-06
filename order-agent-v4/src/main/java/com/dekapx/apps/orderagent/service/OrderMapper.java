package com.dekapx.apps.orderagent.service;

import com.dekapx.apps.orderagent.dto.OrderDtos.OrderCreateRequest;
import com.dekapx.apps.orderagent.dto.OrderDtos.OrderResponse;
import com.dekapx.apps.orderagent.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getItemName(),
                order.getQuantity(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderDate(),
                order.getEstimatedDeliveryDate(),
                order.getTrackingNumber(),
                order.getCarrier(),
                order.getCurrentLocation(),
                order.getDeliveryAddress(),
                order.getCancellationReason()
        );
    }

    public Order toEntity(OrderCreateRequest req) {
        return Order.builder()
                .orderNumber(req.orderNumber())
                .customerName(req.customerName())
                .itemName(req.itemName())
                .quantity(req.quantity())
                .totalAmount(req.totalAmount())
                .status(req.status())
                .orderDate(req.orderDate())
                .estimatedDeliveryDate(req.estimatedDeliveryDate())
                .trackingNumber(req.trackingNumber())
                .carrier(req.carrier())
                .currentLocation(req.currentLocation())
                .deliveryAddress(req.deliveryAddress())
                .cancellationReason(req.cancellationReason())
                .build();
    }
}
