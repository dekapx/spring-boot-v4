package com.dekapx.apps.orderagent.tools;

import com.dekapx.apps.orderagent.dto.OrderDtos.OrderResponse;
import com.dekapx.apps.orderagent.service.OrderNotFoundException;
import com.dekapx.apps.orderagent.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Tools (function calls) that the chat model is allowed to invoke autonomously
 * while acting as the Order Agent. Spring AI discovers these via the {@code @Tool}
 * annotation and exposes their JSON schema to the model.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTools {

    private final OrderService orderService;

    @Tool(description = "Find and return the full details of an order given its order number. " +
            "Use this when the user wants to know everything about an order (items, amount, dates, " +
            "carrier, tracking number, delivery address, etc).")
    public String findOrderDetails(
            @ToolParam(description = "The order number, e.g. ORD-10021") String orderNumber) {
        log.info("[tool] findOrderDetails({})", orderNumber);
        try {
            OrderResponse order = orderService.findByOrderNumber(orderNumber);
            return order.toString();
        } catch (OrderNotFoundException ex) {
            return "No order was found with order number '" + orderNumber + "'.";
        }
    }

    @Tool(description = "Find just the current status of an order given its order number. " +
            "Use this when the user only asks 'where is my order' or 'what is the status of order X'.")
    public String findOrderStatus(
            @ToolParam(description = "The order number, e.g. ORD-10021") String orderNumber) {
        log.info("[tool] findOrderStatus({})", orderNumber);
        try {
            String status = orderService.findStatus(orderNumber);
            return "Order " + orderNumber + " has status: " + status;
        } catch (OrderNotFoundException ex) {
            return "No order was found with order number '" + orderNumber + "'.";
        }
    }

    @Tool(description = "Change/update the delivery address of an existing order. " +
            "Only works if the order has not already been delivered or cancelled. " +
            "Always confirm the new address back to the user in your reply.")
    public String changeDeliveryLocation(
            @ToolParam(description = "The order number, e.g. ORD-10021") String orderNumber,
            @ToolParam(description = "The new full delivery address to set on the order") String newDeliveryAddress) {
        log.info("[tool] changeDeliveryLocation({}, {})", orderNumber, newDeliveryAddress);
        try {
            OrderResponse updated = orderService.changeDeliveryLocation(orderNumber, newDeliveryAddress);
            return "Delivery location for order " + orderNumber + " was updated to: " + updated.deliveryAddress();
        } catch (OrderNotFoundException ex) {
            return "No order was found with order number '" + orderNumber + "'.";
        } catch (IllegalStateException ex) {
            return "Could not update delivery location: " + ex.getMessage();
        }
    }

    @Tool(description = "Search orders belonging to a customer by (partial) customer name. " +
            "Use this when the user does not know their exact order number.")
    public String findOrdersByCustomerName(
            @ToolParam(description = "Full or partial customer name") String customerName) {
        log.info("[tool] findOrdersByCustomerName({})", customerName);
        List<OrderResponse> orders = orderService.findByCustomerName(customerName);
        if (orders.isEmpty()) {
            return "No orders found for customer '" + customerName + "'.";
        }
        return orders.stream()
                .map(o -> o.orderNumber() + " (" + o.status() + ", " + o.itemName() + ")")
                .reduce((a, b) -> a + "; " + b)
                .orElse("No orders found.");
    }
}
