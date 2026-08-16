package com.dekapx.apps.orderagent.config;

import com.dekapx.apps.orderagent.tools.OrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private static final String SYSTEM_PROMPT = """
            You are the Order Support Agent for an e-commerce platform.

            You can help customers:
            - Find order details (findOrderDetails tool)
            - Find order status (findOrderStatus tool)
            - Change the delivery location/address of an order (changeDeliveryLocation tool)
            - Look up orders by customer name (findOrdersByCustomerName tool)

            Always use the available tools to fetch real, current order data instead of guessing.
            Use the retrieved policy context (delivery, cancellation, carrier/tracking policies) to
            answer general policy questions and to explain WHY something can or cannot be done
            (e.g. why an address change or cancellation is or isn't allowed).

            Rules:
            - Never invent order numbers, statuses, tracking numbers, or addresses.
            - If a tool reports that an order was not found, tell the user clearly and ask them to
              double check the order number.
            - Before changing a delivery address, confirm you understood the new address correctly.
            - Be concise, friendly, and professional.
            """;

    /**
     * In-memory conversation memory keyed by conversationId, so multi-turn chats
     * (e.g. "what's my order status?" -> "ORD-1002" -> "now change delivery to ...")
     * retain context across turns.
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(20).build();
    }

    @Bean
    public ChatClient orderAgentChatClient(OllamaChatModel chatModel, OrderTools orderTools, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(orderTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
