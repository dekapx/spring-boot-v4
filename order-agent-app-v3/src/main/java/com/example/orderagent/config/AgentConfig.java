package com.example.orderagent.config;

import com.example.orderagent.tools.OrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    private static final String SYSTEM_PROMPT = """
            You are a helpful customer support agent for an e-commerce order management system.

            You have two kinds of information available to you:

              1. TOOLS, which fetch live, customer-specific order data:
                   - Find order details
                   - Find order status ("where is my order")
                   - Change the delivery location/address of an order
                 Use a tool whenever the customer asks about a specific order, its status,
                 tracking, or wants to change where it's delivered.

              2. RETRIEVED CONTEXT, which will appear below a question when relevant. It
                 comes from our shipping, returns/cancellations, and FAQ documentation.
                 Use it to answer general policy questions such as "how long do returns
                 take" or "can I cancel my order" — do not use it to answer questions
                 about a specific customer's order; use tools for that instead.

            Rules:
              - Never invent order information, statuses, tracking numbers, or policy
                details that aren't backed by a tool result or the retrieved context.
              - If the customer has not given you an order number for an order-specific
                question, politely ask for it before calling any tool.
              - If a tool reports the order was not found, tell the customer clearly and ask
                them to double check the order number.
              - If no retrieved context is present and the question is a general policy
                question (not answerable by a tool), say you're not sure and suggest
                contacting support, rather than guessing.
              - Keep answers concise and friendly.
            """;

    /**
     * Conversation memory: a rolling window of the last 20 messages per conversationId.
     * Backed by the default in-memory repository (auto-configured by Spring AI) — swap
     * in a JDBC-backed ChatMemoryRepository bean for persistence across restarts /
     * multiple instances; this bean definition doesn't need to change either way.
     */
    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }

    /**
     * Modular / "Advanced RAG" pipeline (Spring AI's RetrievalAugmentationAdvisor):
     *
     *   1. RewriteQueryTransformer — before searching, asks the chat model to rewrite the
     *      user's message (using conversation history) into a better standalone search
     *      query. E.g. a follow-up like "and what about electronics?" gets rewritten into
     *      something like "what is the return window for electronics?" using prior turns.
     *
     *   2. VectorStoreDocumentRetriever — runs the (rewritten) query against pgvector,
     *      applying a similarity threshold and top-K cutoff.
     *
     *   3. ContextualQueryAugmenter — injects the retrieved chunks into the prompt as
     *      context. allowEmptyContext(true) is important here: this agent is a *hybrid*
     *      RAG + tool-calling agent, so when a question is about a specific order (which
     *      won't match any knowledge-base document) we must NOT let the advisor swap in
     *      a "cannot answer" refusal prompt — the model still needs to be free to call
     *      OrderTools instead.
     */
    @Bean
    public RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(ChatClient.Builder chatClientBuilder,
                                                                      VectorStore vectorStore) {

        RewriteQueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder.build().mutate())
                .build();

        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.55)
                .topK(4)
                .build();

        ContextualQueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(queryTransformer)
                .documentRetriever(documentRetriever)
                .queryAugmenter(queryAugmenter)
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel, OrderTools orderTools,
                                  ChatMemory chatMemory, RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {

        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(orderTools)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        retrievalAugmentationAdvisor
                )
                .build();
    }
}
