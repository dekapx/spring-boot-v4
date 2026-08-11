package com.example.orderagent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Not part of the agent flow itself — a small utility endpoint to see exactly which
 * knowledge-base chunks a query retrieves, useful when tuning topK/similarityThreshold
 * or debugging why the agent didn't pick up a policy fact.
 */
@RestController
@RequiredArgsConstructor
public class KnowledgeBaseDebugController {

    private final VectorStore vectorStore;

    @GetMapping("/api/knowledge-base/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "4") int topK) {

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query(q).topK(topK).build());

        List<Map<String, Object>> response = results.stream()
                .map(d -> Map.<String, Object>of(
                        "source", d.getMetadata().getOrDefault("source", "unknown"),
                        "score", d.getScore() == null ? 0.0 : d.getScore(),
                        "content", d.getFormattedContent()))
                .toList();

        return ResponseEntity.ok(response);
    }
}
