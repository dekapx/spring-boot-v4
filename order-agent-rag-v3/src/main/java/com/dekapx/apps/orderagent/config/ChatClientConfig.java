package com.dekapx.apps.orderagent.config;

import com.dekapx.apps.orderagent.tools.OrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.dekapx.apps.orderagent.common.SystemPrompts.ORDER_AGENT_SYSTEM_PROMPT;

@Configuration
public class ChatClientConfig {
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient orderAgentChatClient(OllamaChatModel chatModel,
                                           OrderTools orderTools,
                                           ChatMemory chatMemory,
                                           VectorStore orderKnowledgeVectorStore) {
        return ChatClient.builder(chatModel)
                .defaultSystem(ORDER_AGENT_SYSTEM_PROMPT)
                .defaultTools(orderTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        buildQuestionAnswerAdvisor(orderKnowledgeVectorStore),
                        new SimpleLoggerAdvisor())
                .build();
    }

    private QuestionAnswerAdvisor buildQuestionAnswerAdvisor(VectorStore orderKnowledgeVectorStore) {
        QuestionAnswerAdvisor ragAdvisor = QuestionAnswerAdvisor.builder(orderKnowledgeVectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.5)
                        .topK(4)
                        .build())
                .build();
        return ragAdvisor;
    }
}
