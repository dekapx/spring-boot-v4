package com.example.orderagent.controller;

import com.example.orderagent.model.Order;
import com.example.orderagent.service.OrderNotFoundException;
import com.example.orderagent.service.OrderService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderNumber}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    @GetMapping("/{orderNumber}/status")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable String orderNumber) {
        return ResponseEntity.ok(Map.of(
                "orderNumber", orderNumber,
                "status", orderService.getOrderStatus(orderNumber)));
    }

    @PatchMapping("/{orderNumber}/delivery-location")
    public ResponseEntity<Order> changeDeliveryLocation(
            @PathVariable String orderNumber,
            @RequestBody DeliveryLocationRequest request) {
        return ResponseEntity.ok(orderService.changeDeliveryLocation(orderNumber, request.deliveryAddress()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    public record DeliveryLocationRequest(@NotBlank String deliveryAddress) {
    }
}
