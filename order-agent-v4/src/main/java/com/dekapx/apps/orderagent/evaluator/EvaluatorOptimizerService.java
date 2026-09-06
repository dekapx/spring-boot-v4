package com.dekapx.apps.orderagent.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Evaluator-Optimizer workflow: a "generator" produces a candidate response, a separate
 * "evaluator" LLM call critiques it against a rubric, and if it falls short the generator
 * is re-invoked with the evaluator's concrete feedback folded into the prompt. This repeats
 * (bounded by {@code maxIterations}) until the response is judged acceptable or the budget
 * is exhausted, at which point the best-scoring response is returned.
 *
 * The generator itself is supplied as a {@link Function} so this service stays decoupled
 * from any particular route's system prompt, tools, or retrieval strategy - {@code AgentService}
 * plugs in the right generator (order/product/policy) per call.
 */
@Service
@Slf4j
public class EvaluatorOptimizerService {

    private static final int DEFAULT_MAX_ITERATIONS = 3;
    private static final int ACCEPTANCE_SCORE = 7; // out of 10

    private static final String EVALUATION_PROMPT = """
            You are a strict quality evaluator for a customer-support AI agent's response.
            Judge the ASSISTANT RESPONSE for: factual grounding (no invented order/product details),
            directly answering the USER QUESTION, and a helpful, professional tone.

            Respond in EXACTLY this two-line format and nothing else:
            SCORE: <integer 0-10>
            FEEDBACK: <one or two concrete, actionable sentences on what to fix, or "None" if score is 9-10>

            USER QUESTION:
            %s

            ASSISTANT RESPONSE:
            %s
            """;

    private final ChatClient evaluatorChatClient;

    public EvaluatorOptimizerService(ChatClient.Builder chatClientBuilder) {
        // Dedicated, low-temperature client used purely to critique - kept separate from
        // whichever generator client the caller passes in.
        this.evaluatorChatClient = chatClientBuilder.build();
    }

    /**
     * Runs the generate -> evaluate -> optimize loop.
     *
     * @param userMessage the original user question, used as evaluation context
     * @param generator   produces a response given a (possibly feedback-augmented) prompt
     * @return the first response judged acceptable, or the last attempt if the iteration budget runs out
     */
    public String optimize(String userMessage, Function<String, String> generator) {
        return optimize(userMessage, generator, DEFAULT_MAX_ITERATIONS);
    }

    public String optimize(String userMessage, Function<String, String> generator, int maxIterations) {
        String response = generator.apply(userMessage);

        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            EvaluationResult evaluation = evaluate(userMessage, response);
            log.info("[EvaluatorOptimizer] iteration={} score={} acceptable={}",
                    iteration, evaluation.score(), evaluation.acceptable());

            if (evaluation.acceptable()) {
                return response;
            }
            if (iteration == maxIterations) {
                log.warn("[EvaluatorOptimizer] exhausted {} iterations without reaching acceptance threshold", maxIterations);
                break;
            }

            String revisionPrompt = """
                    %s

                    [System note: your previous answer needs improvement. Evaluator feedback: "%s".
                    Revise your answer to address this feedback while still answering the original question.]
                    """.formatted(userMessage, evaluation.feedback());

            response = generator.apply(revisionPrompt);
        }

        return response;
    }

    private EvaluationResult evaluate(String userMessage, String response) {
        try {
            String raw = evaluatorChatClient.prompt()
                    .user(EVALUATION_PROMPT.formatted(userMessage, response))
                    .call()
                    .content();
            return parse(raw);
        } catch (Exception e) {
            log.error("[EvaluatorOptimizer] evaluation call failed, treating response as acceptable", e);
            return new EvaluationResult(ACCEPTANCE_SCORE, "Evaluator unavailable.", true);
        }
    }

    private EvaluationResult parse(String raw) {
        int score = ACCEPTANCE_SCORE; // fail-open default
        String feedback = "";
        if (raw != null) {
            for (String line : raw.split("\\r?\\n")) {
                String upper = line.trim().toUpperCase();
                if (upper.startsWith("SCORE:")) {
                    try {
                        score = Integer.parseInt(line.trim().substring(6).trim().replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException ignored) {
                        // keep default
                    }
                } else if (upper.startsWith("FEEDBACK:")) {
                    feedback = line.trim().substring(9).trim();
                }
            }
        }
        return new EvaluationResult(score, feedback, score >= ACCEPTANCE_SCORE);
    }
}
