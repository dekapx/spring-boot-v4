package com.dekapx.apps.service;

import com.dekapx.apps.agent.AssistantAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final AssistantAgent agent;

    private final List<String> historyMessages = new ArrayList<>();

    public String ask(String question) {
        log.info("[Ollama/gemma4:31b] User: {}", question);
        String response = agent.chat(question);

        log.info("[Ollama/gemma4:31b] Response: {}", response);
        return response;
    }
}