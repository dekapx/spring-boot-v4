package com.dekapx.apps.orderagent.routing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Routing Workflow: a fast, cheap classification step decides which specialized handling
 * path an incoming message should follow, instead of a single monolithic prompt trying to
 * do order lookups, product search, and policy Q&A all at once. This keeps each downstream
 * path's system prompt, tool set, and retrieval filter focused and easier to reason about.
 */
@Component
@Slf4j
public class QueryRouter {

    private static final String ROUTER_PROMPT = """
            Classify the user's message into exactly one of these categories:

            - ORDER_LOOKUP: asking about a specific order's details/status, or asking to change
              a delivery address/location for an order.
            - PRODUCT_SEARCH: describing a need, use case, or looking for product recommendations
              (not referencing an existing order).
            - POLICY_QUESTION: asking about rules/policy - cancellations, returns, delivery timing,
              carriers, "why can't I..." questions - without necessarily naming an order.
            - GENERAL: greetings, small talk, or anything unrelated to orders/products/policy.
            - ESCALATION: complaints, anger, threats, fraud reports, or explicit requests for a
              human/manager.

            Respond with ONLY the category name, nothing else.

            User message: %s
            """;

    private final ChatClient routerChatClient;

    public QueryRouter(ChatClient.Builder chatClientBuilder) {
        // A dedicated, tool-free, low-temperature client purely for classification.
        this.routerChatClient = chatClientBuilder.build();
    }

    public QueryRoute route(String userMessage) {
        try {
            String raw = routerChatClient.prompt()
                    .user(ROUTER_PROMPT.formatted(userMessage))
                    .call()
                    .content();

            String normalized = raw == null ? "" : raw.trim().toUpperCase().replaceAll("[^A-Z_]", "");
            for (QueryRoute route : QueryRoute.values()) {
                if (normalized.contains(route.name())) {
                    return route;
                }
            }
            log.warn("[QueryRouter] could not confidently parse route from '{}', defaulting to ORDER_LOOKUP", raw);
            return QueryRoute.ORDER_LOOKUP;
        } catch (Exception e) {
            log.error("[QueryRouter] routing call failed, defaulting to ORDER_LOOKUP", e);
            return QueryRoute.ORDER_LOOKUP;
        }
    }
}
