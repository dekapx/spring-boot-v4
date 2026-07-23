package com.dekapx.apps.model;

import jakarta.validation.constraints.NotBlank;

public record OrderAgentRequest(@NotBlank(message = "Message should not be blank") String message) {
}
