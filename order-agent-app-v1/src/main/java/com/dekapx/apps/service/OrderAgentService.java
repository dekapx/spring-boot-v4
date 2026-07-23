package com.dekapx.apps.service;

import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderAgentService {
    private static final String PROMPT = """
            You are an order support assistant.
            If the user asks about order location, order status or delivery updates,
            extract the order number and call the available tool to fetch live order data.
            If the order number is missing, ask the user to provide it.
            Keep response short and clear.
            """;

    private final ChatClient chatClient;
    private final OrderAgentTools orderAgentTools;

    public OrderAgentService(ChatClient.Builder builder, OrderAgentTools orderAgentTools) {
        this.chatClient = builder.build();
        this.orderAgentTools = orderAgentTools;
    }

    public String ask(@NotBlank(message = "Message should not be blank") String message) {
        return chatClient
                .prompt(PROMPT)
                .user(message)
                .tools(orderAgentTools)
                .call()
                .content();
    }
}
