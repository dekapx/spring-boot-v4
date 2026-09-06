package com.dekapx.apps.orderagent.routing;

/**
 * The set of intents the {@link QueryRouter} can classify an incoming agent message into.
 * Each route is handled by a differently-configured chat interaction in {@code AgentService}
 * (different system prompt, tool set, and/or retrieval filter) rather than one generic prompt
 * trying to do everything.
 */
public enum QueryRoute {

    /** Looking up or acting on a specific order (details, status, delivery address changes). */
    ORDER_LOOKUP,

    /** Product discovery / recommendation questions best served by semantic product search. */
    PRODUCT_SEARCH,

    /** General policy questions (returns, cancellations, delivery rules) best served by RAG only. */
    POLICY_QUESTION,

    /** Small talk / greetings / anything not order, product, or policy related. */
    GENERAL,

    /** Complaints, threats of legal action, fraud reports, or anything requiring a human agent. */
    ESCALATION
}
