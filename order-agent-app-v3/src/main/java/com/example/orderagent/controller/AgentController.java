package com.example.orderagent.controller;

import com.example.orderagent.dto.ChatRequest;
import com.example.orderagent.dto.ChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final ChatClient chatClient;

    /**
     * Natural-language entry point for the order agent.
     *
     * Example:
     *   POST /api/agent/chat
     *   { "message": "where is my order ORD12345" }
     *
     *   POST /api/agent/chat
     *   { "message": "how long do I have to return an item" }
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String conversationId = (request.conversationId() == null || request.conversationId().isBlank())
                ? "default"
                : request.conversationId();

        var response = chatClient.prompt()
                .user(request.message())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();

        String reply = response.getResult().getOutput().getText();
        List<String> sources = extractSources(response.getResult());

        return ResponseEntity.ok(new ChatResponse(reply, sources));
    }

    /**
     * The RetrievalAugmentationAdvisor stores whatever documents it retrieved (if any)
     * in the result's metadata under RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT.
     * This will be empty/absent for messages the agent answered purely via tool calls
     * (e.g. order lookups), since those don't go through the knowledge-base retriever.
     */
    private List<String> extractSources(Generation result) {
        Object raw = result.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT);
        if (!(raw instanceof List<?> docs)) {
            return List.of();
        }

        Set<String> sources = new LinkedHashSet<>();
        for (Object o : docs) {
            if (o instanceof Document d) {
                sources.add(String.valueOf(d.getMetadata().getOrDefault("source", "knowledge base")));
            }
        }
        return List.copyOf(sources);
    }
}
