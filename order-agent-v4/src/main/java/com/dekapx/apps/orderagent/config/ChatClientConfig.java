package com.dekapx.apps.orderagent.config;

import com.dekapx.apps.orderagent.advisor.RecursiveRetrievalAdvisor;
import com.dekapx.apps.orderagent.tools.OrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private static final String ORDER_SYSTEM_PROMPT = """
            You are the Order Support Agent for an e-commerce platform.

            You can help customers:
            - Find order details (findOrderDetails tool)
            - Find order status (findOrderStatus tool)
            - Change the delivery location/address of an order (changeDeliveryLocation tool)
            - Look up orders by customer name (findOrdersByCustomerName tool)
            - Semantically search the product catalog (semanticProductSearch tool)

            Always use the available tools to fetch real, current order/product data instead of
            guessing. Use any retrieved policy context to explain WHY something can or cannot be
            done (e.g. why an address change or cancellation is or isn't allowed).

            Rules:
            - Never invent order numbers, statuses, tracking numbers, addresses, SKUs, or prices.
            - If a tool reports that an order was not found, tell the user clearly and ask them to
              double check the order number.
            - Before changing a delivery address, confirm you understood the new address correctly.
            - Be concise, friendly, and professional.
            """;

    private static final String POLICY_SYSTEM_PROMPT = """
            You are the Policy Assistant for an e-commerce platform. Answer questions about
            delivery, cancellation, returns, and carrier/tracking policy using ONLY the retrieved
            context provided to you. If the context does not cover the question, say you're not
            sure rather than guessing. Be concise and cite the relevant rule in plain language.
            """;

    private static final String PRODUCT_SYSTEM_PROMPT = """
            You are the Shopping Assistant for an e-commerce platform. Use the
            semanticProductSearch tool to find products matching what the customer describes,
            then summarize the best matches (name, price, why it fits) in a short, friendly reply.
            Never invent products, prices, or stock levels that the tool did not return.
            """;

    /**
     * In-memory conversation memory keyed by conversationId, so multi-turn chats
     * (e.g. "what's my order status?" -> "ORD-1002" -> "now change delivery to ...")
     * retain context across turns.
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(20).build();
    }

    /**
     * Recursive Advisor wired against the "policy" slice of the shared vector store -
     * used for policy-grounded RAG (both directly, and as part of the order agent's
     * general context).
     */
    @Bean
    public RecursiveRetrievalAdvisor policyRecursiveRetrievalAdvisor(VectorStore vectorStore) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression policyFilter = b.eq("docType", "policy").build();
        return new RecursiveRetrievalAdvisor(vectorStore, policyFilter, /*maxDepth*/ 2, /*topKPerQuery*/ 3, /*similarityThreshold*/ 0.5);
    }

    /** Order-handling route: tools (orders + product search) + recursive policy RAG + memory. */
    @Bean
    public ChatClient orderAgentChatClient(OllamaChatModel chatModel, OrderTools orderTools,
                                            ChatMemory chatMemory, RecursiveRetrievalAdvisor policyRecursiveRetrievalAdvisor) {
        return ChatClient.builder(chatModel)
                .defaultSystem(ORDER_SYSTEM_PROMPT)
                .defaultTools(orderTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), policyRecursiveRetrievalAdvisor)
                .build();
    }

    /** Policy route: RAG-only, no tools - grounded purely in the ingested policy documents. */
    @Bean
    public ChatClient policyChatClient(OllamaChatModel chatModel, RecursiveRetrievalAdvisor policyRecursiveRetrievalAdvisor) {
        return ChatClient.builder(chatModel)
                .defaultSystem(POLICY_SYSTEM_PROMPT)
                .defaultAdvisors(policyRecursiveRetrievalAdvisor)
                .build();
    }

    /** Product-search route: only the semantic product search tool, no order tools. */
    @Bean
    public ChatClient productChatClient(OllamaChatModel chatModel, OrderTools orderTools) {
        return ChatClient.builder(chatModel)
                .defaultSystem(PRODUCT_SYSTEM_PROMPT)
                .defaultTools(orderTools)
                .build();
    }

    /**
     * Plain builder (no defaults) exposed for components that need their own lightweight
     * client, e.g. {@code QueryRouter} for intent classification and
     * {@code EvaluatorOptimizerService} for response critique.
     */
    @Bean
    public ChatClient.Builder chatClientBuilder(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }
}
