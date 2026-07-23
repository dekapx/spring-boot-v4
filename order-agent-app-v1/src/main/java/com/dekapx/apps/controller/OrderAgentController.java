package com.dekapx.apps.controller;

import com.dekapx.apps.dto.ChatRequest;
import com.dekapx.apps.dto.ChatResponse;
import com.dekapx.apps.service.OrderAgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Natural-language endpoint. Example:
 *
 * POST /api/agent/chat
 * { "message": "Hey, where is my order ORD-1023?" }
 *
 * -> { "reply": "...", "intent": "ORDER_STATUS", "order": { ... } }
 */
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class OrderAgentController {

    private final OrderAgentService orderAgentService;

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return orderAgentService.handleUserMessage(request.message());
    }
}
