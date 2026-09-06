package com.dekapx.apps.orderagent.controller;

import com.dekapx.apps.orderagent.dto.OrderDtos.ChangeDeliveryLocationRequest;
import com.dekapx.apps.orderagent.dto.OrderDtos.OrderCreateRequest;
import com.dekapx.apps.orderagent.dto.OrderDtos.OrderResponse;
import com.dekapx.apps.orderagent.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.findByOrderNumber(orderNumber));
    }

    @GetMapping("/{orderNumber}/status")
    public ResponseEntity<Map<String, String>> getOrderStatus(@PathVariable String orderNumber) {
        return ResponseEntity.ok(Map.of("orderNumber", orderNumber, "status", orderService.findStatus(orderNumber)));
    }

    @GetMapping(params = "customerName")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@RequestParam String customerName) {
        return ResponseEntity.ok(orderService.findByCustomerName(customerName));
    }

    @PatchMapping("/delivery-location")
    public ResponseEntity<OrderResponse> changeDeliveryLocation(@Valid @RequestBody ChangeDeliveryLocationRequest request) {
        return ResponseEntity.ok(orderService.changeDeliveryLocation(request.orderNumber(), request.newDeliveryAddress()));
    }
}
