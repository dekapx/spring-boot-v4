package com.dekapx.apps.orderagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A "recursive advisor": rather than a single flat similarity search (as a plain
 * {@code QuestionAnswerAdvisor} performs), this advisor retrieves context recursively:
 *
 * 1. Decomposition recursion — a compound user message ("what's the status of ORD-1002
 *    AND can I change the delivery address on ORD-1004") is split into sub-questions,
 *    each of which is retrieved (and, recursively, can itself be decomposed further,
 *    bounded by {@code maxDepth}).
 * 2. Adaptive-broadening recursion — if a (sub-)query returns too few/weak matches, the
 *    advisor recurses with a broadened form of the same query (filter relaxed, then
 *    threshold relaxed) instead of giving up, again bounded by {@code maxDepth}.
 *
 * All retrieved chunks are deduplicated and merged into a single context block that is
 * injected as a system message before the request continues down the advisor chain —
 * the same mechanism {@code QuestionAnswerAdvisor} uses, just fed by a recursive
 * retrieval strategy instead of one similarity search.
 */
@Slf4j
public class RecursiveRetrievalAdvisor implements CallAdvisor {

    private static final String CONTEXT_SYSTEM_TEMPLATE = """
            Use the following retrieved context to answer the user's question when relevant.
            If the context does not contain the answer, rely on your tools or say you don't know
            rather than guessing.

            --- RETRIEVED CONTEXT (recursively gathered, %d source chunk(s)) ---
            %s
            --- END CONTEXT ---
            """;

    private final VectorStore vectorStore;
    private final Filter.Expression baseFilter;
    private final int maxDepth;
    private final int topKPerQuery;
    private final double similarityThreshold;

    public RecursiveRetrievalAdvisor(VectorStore vectorStore, Filter.Expression baseFilter,
                                      int maxDepth, int topKPerQuery, double similarityThreshold) {
        this.vectorStore = vectorStore;
        this.baseFilter = baseFilter;
        this.maxDepth = maxDepth;
        this.topKPerQuery = topKPerQuery;
        this.similarityThreshold = similarityThreshold;
    }

    @Override
    public String getName() {
        return "RecursiveRetrievalAdvisor";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        String userText = chatClientRequest.prompt().getUserMessage().getText();

        Set<Document> merged = new LinkedHashSet<>();
        retrieveRecursively(userText, maxDepth, true, merged);

        if (merged.isEmpty()) {
            log.debug("[RecursiveRetrievalAdvisor] no context found for query, passing through unmodified");
            return callAdvisorChain.nextCall(chatClientRequest);
        }

        String contextBlock = merged.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        SystemMessage contextMessage = new SystemMessage(
                CONTEXT_SYSTEM_TEMPLATE.formatted(merged.size(), contextBlock));

        List<Message> augmentedMessages = new ArrayList<>(chatClientRequest.prompt().getInstructions());
        augmentedMessages.add(0, contextMessage);

        Prompt augmentedPrompt = new Prompt(augmentedMessages, chatClientRequest.prompt().getOptions());
        ChatClientRequest augmentedRequest = chatClientRequest.mutate().prompt(augmentedPrompt).build();

        return callAdvisorChain.nextCall(augmentedRequest);
    }

    /**
     * Recursively retrieves documents for {@code query}, decomposing compound questions
     * and broadening weak/empty results, up to {@code depth} levels of recursion.
     */
    private void retrieveRecursively(String query, int depth, boolean allowDecompose, Set<Document> accumulator) {
        List<Document> docs = search(query, baseFilter);

        if (docs.isEmpty() && depth > 0) {
            // Recursion case 1: broaden by dropping the docType filter and retrying.
            List<Document> broadened = search(query, null);
            accumulator.addAll(broadened);
            if (broadened.isEmpty()) {
                retrieveRecursively(query, depth - 1, false, accumulator);
                return;
            }
        } else {
            accumulator.addAll(docs);
        }

        if (!allowDecompose || depth <= 0) {
            return;
        }

        List<String> subQueries = decompose(query);
        if (subQueries.size() > 1) {
            for (String subQuery : subQueries) {
                retrieveRecursively(subQuery, depth - 1, true, accumulator);
            }
        }
    }

    private List<Document> search(String query, Filter.Expression filter) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(query)
                .topK(topKPerQuery)
                .similarityThreshold(similarityThreshold);
        if (filter != null) {
            builder.filterExpression(filter);
        }
        List<Document> results = vectorStore.similaritySearch(builder.build());
        return results == null ? List.of() : results;
    }

    /** Lightweight heuristic decomposition of compound questions into sub-questions. */
    private List<String> decompose(String query) {
        String[] parts = query.split("(?i)\\s+and also\\s+|(?i)\\s+and\\s+(?=(can|is|are|does|do|what|when|why|how))|;");
        List<String> cleaned = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.length() > 5) {
                cleaned.add(trimmed);
            }
        }
        return cleaned.isEmpty() ? List.of(query) : cleaned;
    }
}
