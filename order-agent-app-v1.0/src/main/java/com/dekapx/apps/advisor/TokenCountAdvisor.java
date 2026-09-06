package com.dekapx.apps.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.Optional;

@Slf4j
public class TokenCountAdvisor implements CallAdvisor {
    private static final int ADVISOR_ORDER = 1;

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        ChatResponse chatResponse = chatClientResponse.chatResponse();

        Optional.ofNullable(chatResponse.getMetadata()).ifPresent(metadata -> {
            Usage usage = metadata.getUsage();
            log.info("Token usage - Prompt tokens: {}, Completion tokens: {}, Total tokens: {}",
                    usage.getPromptTokens(),
                    usage.getCompletionTokens(),
                    usage.getTotalTokens());
        });

        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "TokenCountAdvisor";
    }

    @Override
    public int getOrder() {
        return ADVISOR_ORDER;
    }
}
