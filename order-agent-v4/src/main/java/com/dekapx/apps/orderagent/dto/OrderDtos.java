package com.dekapx.apps.orderagent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderDtos {

    public record OrderCreateRequest(
            @NotBlank String orderNumber,
            @NotBlank String customerName,
            @NotBlank String itemName,
            @NotNull @Positive Integer quantity,
            @NotNull BigDecimal totalAmount,
            @NotBlank String status,
            @NotNull LocalDate orderDate,
            LocalDate estimatedDeliveryDate,
            String trackingNumber,
            String carrier,
            String currentLocation,
            @NotBlank String deliveryAddress,
            String cancellationReason
    ) {}

    public record OrderResponse(
            Long id,
            String orderNumber,
            String customerName,
            String itemName,
            Integer quantity,
            BigDecimal totalAmount,
            String status,
            LocalDate orderDate,
            LocalDate estimatedDeliveryDate,
            String trackingNumber,
            String carrier,
            String currentLocation,
            String deliveryAddress,
            String cancellationReason
    ) {}

    public record ChangeDeliveryLocationRequest(
            @NotBlank String orderNumber,
            @NotBlank String newDeliveryAddress
    ) {}

    public record AgentChatRequest(
            @NotBlank String message,
            String conversationId
    ) {}

    public record AgentChatResponse(
            String reply,
            String conversationId,
            String route
    ) {}
}
