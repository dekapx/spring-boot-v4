package com.dekapx.apps.orderagent.service;

import com.dekapx.apps.orderagent.dto.OrderDtos.AgentChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final ChatClient orderAgentChatClient;
    private final VectorStore vectorStore;

    public AgentChatResponse chat(String userMessage, String conversationId) {
        String convId = (conversationId == null || conversationId.isBlank())
                ? UUID.randomUUID().toString()
                : conversationId;

        QuestionAnswerAdvisor ragAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.5)
                        .topK(4)
                        .build())
                .build();

        String reply = orderAgentChatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, convId))
                .advisors(ragAdvisor)
                .user(userMessage)
                .call()
                .content();

        return new AgentChatResponse(reply, convId);
    }
}
