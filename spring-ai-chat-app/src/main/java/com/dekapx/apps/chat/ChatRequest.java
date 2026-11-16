package com.dekapx.apps.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatRequest(
        @NotBlank(message = "Message must not be blank")
        @Size(max = 10000, message = "Message must not exceed 10,000 characters")
        String message,

        @Valid
        @Size(max = 30, message = "At most 30 previous messages are supported")
        List<ChatMessage> history
) {
    public ChatRequest {
        history = history == null ? List.of() : List.copyOf(history);
    }
}
