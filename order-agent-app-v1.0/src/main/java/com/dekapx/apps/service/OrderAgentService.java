package com.dekapx.apps.service;

import com.dekapx.apps.advisor.TokenCountAdvisor;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dekapx.apps.common.LlmModelConstants.MODEL_NAME;
import static com.dekapx.apps.common.LlmModelConstants.NUM_CTX;
import static com.dekapx.apps.common.LlmModelConstants.NUM_PREDICT;
import static com.dekapx.apps.common.LlmModelConstants.TEMPERATURE;
import static com.dekapx.apps.common.LlmModelConstants.TOP_K;
import static com.dekapx.apps.common.LlmModelConstants.TOP_P;
import static com.dekapx.apps.common.Prompts.ORDER_AGENT_PROMPT;

@Slf4j
@Service
public class OrderAgentService {
    private final ChatClient chatClient;
    private final OrderAgentTools orderAgentTools;

    public OrderAgentService(ChatClient chatClient, OrderAgentTools orderAgentTools) {
        this.chatClient = chatClient;
        this.orderAgentTools = orderAgentTools;
    }

    public String ask(@NotBlank(message = "Message should not be blank") String message) {
        return chatClient
                .prompt(ORDER_AGENT_PROMPT)
                .options(buildOllamaOptions())
                .advisors(getAdvisors())
                .user(message)
                .tools(orderAgentTools)
                .call()
                .content();
    }

    /**
     * Build and return the OllamaOptions as chat Options for Ollama.
     * Best practice is to configure these options in the application.yml file
     *
     * @return object of <code>OllamaOptions</code>
     */
    private OllamaOptions buildOllamaOptions() {
        return OllamaOptions.builder()
                .model(MODEL_NAME)
                .temperature(TEMPERATURE)
                .topP(TOP_P)
                .topK(TOP_K)
                .numPredict(NUM_PREDICT)
                .numCtx(NUM_CTX)
                .build();
    }

    private List<Advisor> getAdvisors() {
        return List.of(
                new SimpleLoggerAdvisor(),
                new TokenCountAdvisor()
        );
    }
}
