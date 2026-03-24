package com.dekapx.apps.config;

import com.dekapx.apps.chat.Assistant;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfig {
    @Bean
    public Assistant assistant(ChatModel model) {
        return AiServices.create(Assistant.class, model);
    }
}
