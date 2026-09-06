package com.dekapx.apps.orderagent.service;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNumber) {
        super("Order not found for orderNumber: " + orderNumber);
    }
}
