package com.example.orderagent.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "message must not be blank") String message,

        // Lets a client keep separate conversation threads (e.g. one per customer
        // session). Spring AI 2.0 requires a conversation ID be supplied on every
        // call to the memory advisor, so this is never left unset internally —
        // AgentController falls back to "default" when the caller omits it.
        String conversationId
) {
}
