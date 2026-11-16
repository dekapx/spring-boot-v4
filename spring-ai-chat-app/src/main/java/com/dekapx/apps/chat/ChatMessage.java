package com.dekapx.apps.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatMessage(
        @NotBlank String role,
        @NotBlank String content
) {
}
