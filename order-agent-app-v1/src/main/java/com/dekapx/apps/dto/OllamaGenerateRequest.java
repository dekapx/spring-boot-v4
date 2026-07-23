package com.dekapx.apps.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Request payload for Ollama's /api/generate endpoint.
 * See: https://github.com/ollama/ollama/blob/main/docs/api.md
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OllamaGenerateRequest(
        String model,
        String prompt,
        boolean stream,
        String format // "json" when we want structured output, null otherwise
) {
    public static OllamaGenerateRequest of(String model, String prompt) {
        return new OllamaGenerateRequest(model, prompt, false, null);
    }

    public static OllamaGenerateRequest jsonMode(String model, String prompt) {
        return new OllamaGenerateRequest(model, prompt, false, "json");
    }
}
