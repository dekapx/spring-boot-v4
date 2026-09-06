package com.dekapx.apps.orderagent.service;

import com.dekapx.apps.orderagent.dto.OrderDtos.AgentChatResponse;
import com.dekapx.apps.orderagent.evaluator.EvaluatorOptimizerService;
import com.dekapx.apps.orderagent.routing.QueryRoute;
import com.dekapx.apps.orderagent.routing.QueryRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

/**
 * Front door for the Order Agent. Combines all seven AI capabilities:
 *
 *  1. Retrieval-Augmented Generation  - {@code policyChatClient} / {@code orderAgentChatClient}
 *     ground answers in ingested policy documents.
 *  2. Vector Store Integration        - shared pgvector store, filtered by {@code docType}
 *     metadata (see {@code ChatClientConfig}, {@code ProductSearchService}).
 *  3. ETL Document Pipeline           - {@code etl.IngestionOrchestrator} loads policy docs and
 *     the product catalog into the vector store on startup.
 *  4. Semantic Product Search         - {@code semanticProductSearch} tool / {@code ProductSearchService}.
 *  5. Recursive Advisors              - {@code RecursiveRetrievalAdvisor} performs multi-hop,
 *     adaptive-depth retrieval instead of one flat similarity search.
 *  6. Routing Workflows               - {@code QueryRouter} classifies intent and this class
 *     dispatches to a specialized chat client per route.
 *  7. Evaluator-Optimizer Pattern     - {@code EvaluatorOptimizerService} critiques and
 *     iteratively refines the generated response before it's returned.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    private final ChatClient orderAgentChatClient;
    private final ChatClient policyChatClient;
    private final ChatClient productChatClient;
    private final QueryRouter queryRouter;
    private final EvaluatorOptimizerService evaluatorOptimizerService;

    public AgentChatResponse chat(String userMessage, String conversationId) {
        String convId = (conversationId == null || conversationId.isBlank())
                ? UUID.randomUUID().toString()
                : conversationId;

        QueryRoute route = queryRouter.route(userMessage);
        log.info("[AgentService] routed message to {}", route);

        String reply = switch (route) {
            case ORDER_LOOKUP -> handleOrderLookup(userMessage, convId);
            case PRODUCT_SEARCH -> handleProductSearch(userMessage);
            case POLICY_QUESTION -> handlePolicyQuestion(userMessage);
            case GENERAL -> handleGeneral(userMessage, convId);
            case ESCALATION -> handleEscalation();
        };

        return new AgentChatResponse(reply, convId, route.name());
    }

    /** Order lookups/actions: tool-calling + recursive policy RAG + memory, quality-checked. */
    private String handleOrderLookup(String userMessage, String conversationId) {
        Function<String, String> generator = prompt -> orderAgentChatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(prompt)
                .call()
                .content();

        // Tool-calling turns are already grounded in real data; a small refinement budget is enough.
        return evaluatorOptimizerService.optimize(userMessage, generator, 2);
    }

    /** Product discovery: semantic search tool, quality-checked so recommendations stay grounded. */
    private String handleProductSearch(String userMessage) {
        Function<String, String> generator = prompt -> productChatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return evaluatorOptimizerService.optimize(userMessage, generator);
    }

    /** Pure policy questions: RAG-only (no tools), quality-checked against the retrieved context. */
    private String handlePolicyQuestion(String userMessage) {
        Function<String, String> generator = prompt -> policyChatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return evaluatorOptimizerService.optimize(userMessage, generator);
    }

    /** Small talk - no need for tools, RAG, or the evaluator loop. */
    private String handleGeneral(String userMessage, String conversationId) {
        return orderAgentChatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(userMessage)
                .call()
                .content();
    }

    /** Fast, deterministic path - no LLM call needed for a handoff message. */
    private String handleEscalation() {
        return "I'm sorry for the trouble - this sounds like something a member of our support "
                + "team should handle personally. I'm flagging this conversation for a human agent "
                + "to follow up with you as soon as possible.";
    }
}
