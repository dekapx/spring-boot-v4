package com.dekapx.apps.controller;

import com.dekapx.apps.chat.Assistant;
import com.dekapx.apps.model.ChatRequest;
import com.dekapx.apps.model.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class OpenApiController {
    private final Assistant aiAssistant;

    public OpenApiController(Assistant aiAssistant) {
        this.aiAssistant = aiAssistant;
    }

    @PostMapping("/chat")
    public ChatResponse chatWithContext(@RequestBody ChatRequest request) {
        String response = aiAssistant.chat(request.message());
        return new ChatResponse(response);
    }

    @GetMapping(value = "/info")
    public String getInfo() {
        log.info("OpenApiController v1.0");
        return "OpenApiController v1.0";
    }
}
