package com.dekapx.apps.service;

import com.dekapx.apps.advisor.TokenCountAdvisor;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
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
                .advisors(getAdvisors())
                .user(message)
                .tools(orderAgentTools)
                .call()
                .content();
    }

    private List<Advisor> getAdvisors() {
        return List.of(
                new SimpleLoggerAdvisor(),
                new TokenCountAdvisor()
        );
    }
}
