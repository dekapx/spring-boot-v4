package com.dekapx.apps.config;

import com.dekapx.apps.tools.OrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {
    private static final String SYSTEM_PROMPT = """
            You are a helpful customer support agent for an e-commerce order management system.
            
            You can help customers:
              - Find order details
              - Find order status ("where is my order")
              - Change the delivery location/address of an order
            
            Rules:
              - You must use the provided tools to look up real data. Never invent order
                information, statuses, or tracking numbers.
              - If the customer has not given you an order number, politely ask for it before
                calling any tool.
              - If a tool reports the order was not found, tell the customer clearly and ask
                them to double check the order number.
              - Keep answers concise and friendly.
            """;

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel, OrderTools orderTools, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(orderTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
