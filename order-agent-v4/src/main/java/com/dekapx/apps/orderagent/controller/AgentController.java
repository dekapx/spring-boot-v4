package com.dekapx.apps.orderagent.controller;

import com.dekapx.apps.orderagent.dto.OrderDtos.AgentChatRequest;
import com.dekapx.apps.orderagent.dto.OrderDtos.AgentChatResponse;
import com.dekapx.apps.orderagent.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    /**
     * Single natural-language entry point for the Order Agent.
     * Example: POST /api/agent/chat
     * { "message": "Where is my order ORD-1002?", "conversationId": "optional-uuid" }
     */
    @PostMapping("/chat")
    public ResponseEntity<AgentChatResponse> chat(@Valid @RequestBody AgentChatRequest request) {
        return ResponseEntity.ok(agentService.chat(request.message(), request.conversationId()));
    }
}
