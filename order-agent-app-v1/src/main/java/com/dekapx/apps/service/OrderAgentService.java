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
                .model("gemma4:31b")
                .temperature(0.7)
                .topP(0.9)
                .topK(40)
                .build();
    }

    private List<Advisor> getAdvisors() {
        return List.of(
                new SimpleLoggerAdvisor(),
                new TokenCountAdvisor()
        );
    }
}
