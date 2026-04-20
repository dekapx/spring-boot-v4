package com.dekapx.apps.controller;

import com.dekapx.apps.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @Value("${langchain4j.ollama.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.ollama.chat-model.base-url}")
    private String baseUrl;

    @PostMapping("/ask")
    public AgentResponse ask(@RequestBody AgentRequest request) {
        return new AgentResponse(agentService.ask(request.question()), modelName);
    }

    @GetMapping("/info")
    public ModelInfo info() {
        log.info("Model Name: {}, Base URL: {}", modelName, baseUrl);
        return new ModelInfo(modelName, baseUrl, "ollama");
    }

    record AgentRequest(String question) {}
    record AgentResponse(String answer, String model) {}
    record ModelInfo(String model, String endpoint, String provider) {}
}
