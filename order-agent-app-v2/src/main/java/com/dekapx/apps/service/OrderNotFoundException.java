package com.dekapx.apps.service;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNumber) {
        super("No order found with order number: " + orderNumber);
    }
}
