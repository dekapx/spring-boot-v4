package com.dekapx.apps.service;

import com.dekapx.apps.dto.OllamaGenerateRequest;
import com.dekapx.apps.dto.OllamaGenerateResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Thin wrapper around the Ollama /api/generate endpoint.
 * Ollama must be running locally (default http://localhost:11434) with a model pulled,
 * e.g.:  ollama pull llama3.1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaService {

    private final WebClient ollamaWebClient;
    private final ObjectMapper objectMapper;

    @Value("${ollama.model}")
    private String model;

    @Value("${ollama.timeout-seconds:60}")
    private long timeoutSeconds;

    /**
     * Asks the LLM to pull structured intent + entities out of a free-text user message.
     * Returns raw JSON text such as: {"intent":"ORDER_STATUS","orderNumber":"ORD-1023"}
     */
    public JsonNode extractIntent(String userMessage) {
        String prompt = """
                You are an intent and entity extraction engine for an e-commerce order support system.
                Read the user's message and return ONLY a JSON object (no prose, no markdown fences) with:
                  - "intent": one of ["ORDER_STATUS", "ORDER_LIST", "GENERAL"]
                  - "orderNumber": the order number/id mentioned in the message, or null if none is present
                  - "customerName": a customer name if mentioned, or null

                Rules:
                - "ORDER_STATUS" = the user is asking about the status/location/delivery of a specific order (e.g. "where is my order", "track order 1023").
                - "ORDER_LIST" = the user wants to see all their orders without specifying one order number.
                - "GENERAL" = anything else (greetings, unrelated questions).
                - Order numbers may look like "1023", "ORD-1023", "#1023". Normalize to the raw token found in the text.

                User message: "%s"

                JSON:
                """.formatted(userMessage);

        OllamaGenerateRequest request = OllamaGenerateRequest.jsonMode(model, prompt);
        OllamaGenerateResponse response = callOllama(request);

        try {
            return objectMapper.readTree(response.response());
        } catch (Exception e) {
            log.warn("Failed to parse Ollama JSON intent response: {}", response.response(), e);
            return objectMapper.createObjectNode().put("intent", "GENERAL");
        }
    }

    /**
     * Given order data (already fetched from Postgres) and the original user question,
     * asks the LLM to produce a friendly, natural-language answer.
     */
    public String composeOrderStatusReply(String userMessage, String orderContextJson) {
        String prompt = """
                You are a helpful, concise customer support agent for an online store.
                The user asked: "%s"

                Here is the verified order data retrieved from our database (JSON):
                %s

                Write a short, friendly reply (2-4 sentences) answering the user's question using ONLY
                the data provided above. Mention the order status, current location/tracking info if present,
                and estimated delivery date if present. Do not invent any details not present in the JSON.
                """.formatted(userMessage, orderContextJson);

        OllamaGenerateRequest request = OllamaGenerateRequest.of(model, prompt);
        OllamaGenerateResponse response = callOllama(request);
        return response.response() != null ? response.response().trim() : "";
    }

    /**
     * Fallback conversational reply for general/non-order messages, or when no order is found.
     */
    public String composeGeneralReply(String userMessage) {
        String prompt = """
                You are a helpful customer support agent for an online store called "ShopFast".
                Reply briefly (1-3 sentences) to the following user message. If it's a greeting, greet back.
                If it seems to be about an order but no order number was found, politely ask the user
                to provide their order number.

                User message: "%s"
                """.formatted(userMessage);

        OllamaGenerateRequest request = OllamaGenerateRequest.of(model, prompt);
        OllamaGenerateResponse response = callOllama(request);
        return response.response() != null ? response.response().trim() : "";
    }

    private OllamaGenerateResponse callOllama(OllamaGenerateRequest request) {
        try {
            return ollamaWebClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OllamaGenerateResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
        } catch (Exception e) {
            log.error("Error calling Ollama at model={}", model, e);
            return new OllamaGenerateResponse(model, "", true);
        }
    }
}
