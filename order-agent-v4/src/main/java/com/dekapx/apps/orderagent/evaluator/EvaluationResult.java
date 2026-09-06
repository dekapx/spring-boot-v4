package com.dekapx.apps.orderagent.evaluator;

/**
 * The evaluator's verdict on a candidate response.
 *
 * @param score       0-10 quality score
 * @param feedback    concrete, actionable feedback for the optimizer to address
 * @param acceptable  true once the response meets the acceptance threshold
 */
public record EvaluationResult(int score, String feedback, boolean acceptable) {
}
