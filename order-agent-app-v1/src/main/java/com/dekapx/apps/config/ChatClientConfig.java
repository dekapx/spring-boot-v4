package com.dekapx.apps.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.PostgresChatMemoryRepositoryDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ChatClientConfig {
    /**
     * Creates a ChatMemoryRepository bean using JdbcTemplate and PostgresChatMemoryRepositoryDialect.
     * It will also create and store the chat memory in spring_ai_chat_memory table in PostgreSQL database.
     *
     * @param jdbcTemplate <code>JdbcTemplate</code> instance for database operations.
     * @return <code>ChatMemoryRepository</code> instance.
     */
    @Bean
    public ChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(new PostgresChatMemoryRepositoryDialect())
                .build();
    }

    /**
     * Creates a ChatMemory bean using the provided ChatMemoryRepository.
     * The chat memory will store a maximum of 10 messages.
     *
     * @param chatMemoryRepository <code>ChatMemoryRepository</code> instance for storing chat memory.
     * @return <code>ChatMemory</code> instance.
     */
    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    /**
     * Creates a ChatClient bean using the provided ChatClient.Builder and ChatMemory.
     * The ChatClient will use the MessageChatMemoryAdvisor to manage chat memory.
     *
     * @param builder    <code>ChatClient.Builder</code> instance for building the ChatClient.
     * @param chatMemory <code>ChatMemory</code> instance for managing chat memory.
     * @return <code>ChatClient</code> instance.
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
