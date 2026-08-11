package com.example.orderagent.tools;

import com.example.orderagent.model.Order;
import com.example.orderagent.service.OrderNotFoundException;
import com.example.orderagent.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * These methods are exposed to the Ollama chat model as callable "tools" (function calling).
 * The model decides, based on the user's natural-language message, which of these
 * to invoke and with what arguments. Spring AI handles the JSON schema generation,
 * the tool-call round trip, and feeding the result back into the model for a final
 * natural-language answer.
 *
 * Keep the returned Strings human-readable — they get fed straight back into the LLM,
 * which then paraphrases them for the end user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderTools {

    private final OrderService orderService;

    @Tool(description = "Get the full details of an order (item, quantity, amount, dates, " +
            "carrier, tracking number, current location, delivery address) given its order number.")
    public String getOrderDetails(
            @ToolParam(description = "The order number, e.g. ORD12345") String orderNumber) {
        log.info("Tool call: getOrderDetails({})", orderNumber);
        try {
            Order o = orderService.getOrderByNumber(orderNumber);
            return """
                    Order Number: %s
                    Customer: %s
                    Item: %s (qty %d)
                    Total Amount: %s
                    Status: %s
                    Order Date: %s
                    Estimated Delivery Date: %s
                    Tracking Number: %s
                    Carrier: %s
                    Current Location: %s
                    Delivery Address: %s
                    """.formatted(
                    o.getOrderNumber(), o.getCustomerName(), o.getItemName(), o.getQuantity(),
                    o.getTotalAmount(), o.getStatus(), o.getOrderDate(), o.getEstimatedDeliveryDate(),
                    o.getTrackingNumber(), o.getCarrier(), o.getCurrentLocation(), o.getDeliveryAddress());
        } catch (OrderNotFoundException e) {
            return "No order was found with order number '" + orderNumber + "'. " +
                    "Ask the customer to double check the order number.";
        }
    }

    @Tool(description = "Get only the current status and location of an order, useful when the " +
            "customer asks 'where is my order' or 'what is the status of my order'.")
    public String getOrderStatus(
            @ToolParam(description = "The order number, e.g. ORD12345") String orderNumber) {
        log.info("Tool call: getOrderStatus({})", orderNumber);
        try {
            Order o = orderService.getOrderByNumber(orderNumber);
            return "Order %s is currently '%s'. Current location: %s. Estimated delivery date: %s. Carrier: %s, tracking number: %s."
                    .formatted(o.getOrderNumber(), o.getStatus(), o.getCurrentLocation(),
                            o.getEstimatedDeliveryDate(), o.getCarrier(), o.getTrackingNumber());
        } catch (OrderNotFoundException e) {
            return "No order was found with order number '" + orderNumber + "'. " +
                    "Ask the customer to double check the order number.";
        }
    }

    @Tool(description = "Change the delivery address of an order that has not yet been delivered " +
            "or cancelled. Use this when the customer asks to change, update, or redirect where " +
            "their order should be delivered.")
    public String changeDeliveryLocation(
            @ToolParam(description = "The order number, e.g. ORD12345") String orderNumber,
            @ToolParam(description = "The new full delivery address") String newAddress) {
        log.info("Tool call: changeDeliveryLocation({}, {})", orderNumber, newAddress);
        try {
            Order updated = orderService.changeDeliveryLocation(orderNumber, newAddress);
            return "Delivery address for order " + updated.getOrderNumber() +
                    " has been updated to: " + updated.getDeliveryAddress();
        } catch (OrderNotFoundException e) {
            return "No order was found with order number '" + orderNumber + "'. " +
                    "Ask the customer to double check the order number.";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }
}
