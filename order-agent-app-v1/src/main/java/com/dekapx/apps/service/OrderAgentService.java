package com.dekapx.apps.service;

import com.dekapx.apps.dto.ChatResponse;
import com.dekapx.apps.model.Order;
import com.dekapx.apps.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderAgentService {

    private final OllamaService ollamaService;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    // Fallback regex in case the LLM is unavailable or misses the entity,
    // e.g. matches "1023", "ORD-1023", "#1023", "order 1023"
    private static final Pattern ORDER_NUMBER_PATTERN =
            Pattern.compile("(?i)(?:ORD-?)?#?\\b(\\d{3,})\\b");

    public ChatResponse handleUserMessage(String userMessage) {
        JsonNode intentJson = ollamaService.extractIntent(userMessage);
        String intent = textOrDefault(intentJson, "intent", "GENERAL");
        String orderNumber = textOrDefault(intentJson, "orderNumber", null);
        String customerName = textOrDefault(intentJson, "customerName", null);

        // Fallback: if the LLM didn't find an order number but the raw text has one, use regex
        if (isBlank(orderNumber)) {
            orderNumber = extractOrderNumberFallback(userMessage);
            if (orderNumber != null && "GENERAL".equals(intent)) {
                intent = "ORDER_STATUS";
            }
        }

        return switch (intent) {
            case "ORDER_STATUS" -> handleOrderStatus(userMessage, orderNumber);
            case "ORDER_LIST" -> handleOrderList(userMessage, customerName);
            default -> handleGeneral(userMessage);
        };
    }

    private ChatResponse handleOrderStatus(String userMessage, String orderNumber) {
        if (isBlank(orderNumber)) {
            String reply = "Could you share your order number so I can look that up for you? "
                    + "It usually looks like ORD-1023 or just the digits, e.g. 1023.";
            return new ChatResponse(reply, "ORDER_STATUS", null);
        }

        String normalized = normalizeOrderNumber(orderNumber);
        Optional<Order> orderOpt = orderRepository.findByOrderNumberIgnoreCase(normalized);

        if (orderOpt.isEmpty()) {
            String reply = "I couldn't find any order matching \"" + orderNumber
                    + "\". Could you double-check the order number?";
            return new ChatResponse(reply, "ORDER_STATUS", null);
        }

        Order order = orderOpt.get();
        String orderJson = toJsonSafely(order);
        String reply = ollamaService.composeOrderStatusReply(userMessage, orderJson);

        if (isBlank(reply)) {
            // Ollama unreachable/failed - deterministic fallback so the API still answers
            reply = buildDeterministicStatusReply(order);
        }

        return new ChatResponse(reply, "ORDER_STATUS", order);
    }

    private ChatResponse handleOrderList(String userMessage, String customerName) {
        if (isBlank(customerName)) {
            String reply = "Sure — what name is the order placed under?";
            return new ChatResponse(reply, "ORDER_LIST", null);
        }

        List<Order> orders = orderRepository.findByCustomerNameIgnoreCaseContaining(customerName);
        if (orders.isEmpty()) {
            String reply = "I couldn't find any orders under the name \"" + customerName + "\".";
            return new ChatResponse(reply, "ORDER_LIST", null);
        }

        StringBuilder sb = new StringBuilder("Here are the orders I found for " + customerName + ": ");
        for (Order o : orders) {
            sb.append(String.format("%s (%s), ", o.getOrderNumber(), o.getStatus()));
        }
        return new ChatResponse(sb.toString(), "ORDER_LIST", orders.get(0));
    }

    private ChatResponse handleGeneral(String userMessage) {
        String reply = ollamaService.composeGeneralReply(userMessage);
        if (isBlank(reply)) {
            reply = "Hi! I'm your order assistant. Ask me things like \"Where is my order ORD-1023?\"";
        }
        return new ChatResponse(reply, "GENERAL", null);
    }

    private String buildDeterministicStatusReply(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your order ").append(order.getOrderNumber())
                .append(" is currently ").append(order.getStatus()).append(".");
        if (order.getCurrentLocation() != null) {
            sb.append(" Last known location: ").append(order.getCurrentLocation()).append(".");
        }
        if (order.getEstimatedDeliveryDate() != null) {
            sb.append(" Estimated delivery: ").append(order.getEstimatedDeliveryDate()).append(".");
        }
        if (order.getTrackingNumber() != null) {
            sb.append(" Tracking number: ").append(order.getTrackingNumber()).append(".");
        }
        return sb.toString();
    }

    private String extractOrderNumberFallback(String message) {
        Matcher matcher = ORDER_NUMBER_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String normalizeOrderNumber(String raw) {
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return raw.trim();
        }
        return "ORD-" + digits;
    }

    private String textOrDefault(JsonNode node, String field, String defaultValue) {
        if (node == null || !node.hasNonNull(field)) {
            return defaultValue;
        }
        String value = node.get(field).asText();
        return "null".equalsIgnoreCase(value) ? defaultValue : value;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String toJsonSafely(Order order) {
        try {
            return objectMapper.writeValueAsString(order);
        } catch (Exception e) {
            log.warn("Failed to serialize order to JSON", e);
            return "{}";
        }
    }
}
