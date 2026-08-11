package com.example.orderagent.dto;

import java.util.List;

public record ChatResponse(
        String reply,

        // Filenames of knowledge-base documents the RAG advisor actually retrieved and
        // used to ground this answer. Empty when the answer came purely from tool calls
        // (order lookups) or general conversation, i.e. no RAG context was used.
        List<String> sources
) {
}
