package com.dekapx.apps.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AssistantAgent {

    @SystemMessage("""
        You are a helpful assistant running locally via Ollama on Gemma 4 31B.
        You have access to tools — use them when the user asks for calculations,
        time, dates, or weather. Be concise and accurate.
        """)
    String chat(String userMessage);
}
