package com.dekapx.apps.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LangChain4jConfig {
    @Value("${app.ollama.chat-model.base-url}")
    private String ollamaBaseUrl;

    @Value("${app.ollama.chat-model.model-name}")
    private String ollamaModelName;

    @Bean
    public OllamaChatModel chatModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModelName)
                .temperature(0.7)
                .build();
    }
}
