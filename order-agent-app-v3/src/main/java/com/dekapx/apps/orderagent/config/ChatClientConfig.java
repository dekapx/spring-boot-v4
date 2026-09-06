package com.dekapx.apps.orderagent.config;

import com.dekapx.apps.orderagent.tools.OrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.dekapx.apps.orderagent.common.SystemPrompts.ORDER_AGENT_SYSTEM_PROMPT;

@Configuration
public class ChatClientConfig {
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
                .defaultSystem(ORDER_AGENT_SYSTEM_PROMPT)
                .defaultTools(orderTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
