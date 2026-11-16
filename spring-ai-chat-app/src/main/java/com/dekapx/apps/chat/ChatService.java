package com.dekapx.apps.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ChatResponse chat(ChatRequest request) {
        List<Message> conversation = new ArrayList<>();

        // Only user and assistant turns are accepted from the browser.
        for (ChatMessage item : request.history()) {
            String role = item.role().toLowerCase(Locale.ROOT);
            switch (role) {
                case "user" -> conversation.add(new UserMessage(item.content()));
                case "assistant" -> conversation.add(new AssistantMessage(item.content()));
                default -> throw new IllegalArgumentException(
                        "Unsupported history role: " + item.role());
            }
        }

        conversation.add(new UserMessage(request.message()));

        String answer = chatClient.prompt()
                .messages(conversation)
                .call()
                .content();

        return new ChatResponse(answer == null ? "" : answer);
    }
}
