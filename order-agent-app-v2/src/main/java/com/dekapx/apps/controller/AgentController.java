package com.dekapx.apps.controller;

import com.dekapx.apps.dto.ChatRequest;
import com.dekapx.apps.dto.ChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     *   { "message": "please change the delivery address for ORD12345 to 22 Baker Street, Dublin" }
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String conversationId = (request.conversationId() == null || request.conversationId().isBlank())
                ? "default"
                : request.conversationId();

        String reply = chatClient.prompt()
                .user(request.message())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
