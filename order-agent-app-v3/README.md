# Order Agent — Spring Boot 4 + Postgres + Ollama (gemma4:31b) + RAG + Chat Memory

A REST-exposed customer support agent that answers natural-language questions about
orders ("where is my order?"), backed by Postgres data and a local Ollama LLM
(**gemma4:31b**) with **tool/function calling**, **RAG** over a policy knowledge base,
and **chat memory** — all on **Spring Boot 4.1 / Spring AI 2.0**.

- **Tool calling**: the model decides which backend method to call (find order details,
  find order status, change delivery location), Spring AI executes it against Postgres,
  and the result is fed back to the model for a natural-language reply.
- **RAG**: general policy/FAQ questions ("how long do I have to return an item?") are
  answered from a knowledge base of markdown docs, embedded and stored in **pgvector**,
  retrieved by similarity search, and injected into the prompt.
- **Chat memory**: conversation history is kept per `conversationId`, so follow-up
  questions ("what about electronics?") are understood in context.

## Stack versions (verified current as of this writing)

| Component      | Version   | Notes                                                       |
|-----------------|-----------|---------------------------------------------------------------|
| Spring Boot     | 4.1.0     | Recommended landing spot — 4.0.x loses OSS support Dec 2026    |
| Spring Framework| 7.0       | Pulled in transitively via the Boot 4 parent                  |
| Spring AI       | 2.0.0     | The 2.x branch targets Spring Boot 4.x / Spring Framework 7   |
| Java            | 17        | Boot 4's baseline is still Java 17 (Java 25 also supported)    |
| Ollama chat model | gemma4:31b | ~20GB dense model — see hardware note below                |
| Ollama embedding model | nomic-embed-text | 768-dim, used for RAG                                |

## How it works

```
User message ("where is my order ORD12345?" / "how long do returns take?")
        │
        ▼
POST /api/agent/chat
        │
        ▼
ChatClient (Spring AI)
   ├─ MessageChatMemoryAdvisor       → loads/saves conversation history for this
   │                                   conversationId (chat memory)
   ├─ RetrievalAugmentationAdvisor   → the RAG pipeline:
   │     1. RewriteQueryTransformer    rewrites the message into a better standalone
   │                                   search query, using conversation history
   │     2. VectorStoreDocumentRetriever  embeds the rewritten query, searches pgvector
   │                                      (topK=4, similarity ≥ 0.55)
   │     3. ContextualQueryAugmenter      injects any matched chunks into the prompt as
   │                                      context. allowEmptyContext=true, so an
   │                                      order-specific question (no KB match) doesn't
   │                                      get blocked — the model can still call a tool
   └─ Tools (OrderTools)             → available for the model to call if the question
                                        needs live, customer-specific order data
        │
        ▼
gemma4:31b (via Ollama) either:
   a) answers directly from the injected policy context, or
   b) calls a tool, e.g. getOrderStatus(orderNumber='ORD12345') → Postgres `orders` table
        │
        ▼
Model composes a friendly natural-language reply →
returned as JSON: { "reply": "...", "sources": ["returns-and-cancellations.md"] }
```

`sources` lists the knowledge-base filenames actually retrieved and used for that
answer, so a client can show "based on our returns policy" style citations. It's an
empty list when the answer came from a tool call or general conversation.

### Two data sources, one agent

| Question type                                   | Source                                   |
|--------------------------------------------------|-------------------------------------------|
| "Where is order ORD12345?"                       | Tool call → `orders` table (live data)   |
| "What's the status of ORD12345?"                 | Tool call → `orders` table                |
| "Change delivery address for ORD12345 to..."     | Tool call → `orders` table (write)        |
| "How long do returns take?"                      | RAG → `knowledge-base/*.md` (pgvector)    |
| "Can I still cancel my order?"                   | RAG → `knowledge-base/*.md` (pgvector)    |
| "Do you ship internationally?"                   | RAG → `knowledge-base/*.md` (pgvector)    |

## Prerequisites

1. **Java 17+** and **Maven 3.9+**
2. **Ollama 0.22+** (needed for Gemma 4 support), installed and running:
   https://ollama.com
   ```bash
   ollama pull gemma4:31b        # chat model — ~20GB, dense 31B parameters
   ollama pull nomic-embed-text  # embedding model — used for RAG
   ollama serve                  # usually already running as a background service
   ```
   **Hardware note**: `gemma4:31b` is the flagship dense Gemma 4 variant and needs a
   workstation-class GPU (or a lot of patience on CPU). If that's too heavy for your
   machine, swap to a lighter Gemma 4 tag — `gemma4:e4b` (default, ~9.6GB) or
   `gemma4:26b` (MoE, ~18GB, faster than its size suggests since only ~4B parameters
   activate per token) — by changing `spring.ai.ollama.chat.model` in
   `application.yml`. Any tag works as long as it supports tool calling.
3. **Postgres with the `pgvector` extension** — the included `docker-compose.yml` uses
   the `pgvector/pgvector:pg16` image:
   ```bash
   docker compose up -d
   ```
   If you're pointing at an existing Postgres instance instead, install the
   [pgvector extension](https://github.com/pgvector/pgvector) on it first — Spring AI
   runs `CREATE EXTENSION IF NOT EXISTS vector` automatically on startup, but the
   extension binary itself must already be installed on the Postgres server.

## Running

```bash
mvn spring-boot:run
```

On startup the app:
1. Creates/updates the `orders` table (`ddl-auto: update`) and seeds three sample
   orders from `data.sql` (`ORD12345`, `ORD67890`, `ORD24680`).
2. Creates the `vector_store` table (pgvector) if it doesn't exist.
3. `KnowledgeBaseIngestionRunner` embeds and loads the markdown files under
   `src/main/resources/knowledge-base/` into the vector store — but only the first
   time (it skips ingestion if `vector_store` already has rows).

The app listens on `http://localhost:8080`.

## Using the agent

### Natural language, with chat memory

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "where is my order ORD12345?"}'
```

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "please change delivery address for ORD24680 to 22 Baker Street, Dublin"}'
```

Follow-ups in the same conversation remember prior context — pass the same
`conversationId`:

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "where is my order?", "conversationId": "session-abc"}'

curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "it is ORD12345", "conversationId": "session-abc"}'
```
The model asks for the order number in the first call (none was given) and, thanks to
chat memory, uses it correctly once supplied in the second.

### RAG / policy questions (same endpoint)

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "how long do I have to return an item?"}'
```

```json
{
  "reply": "You have 30 days from the delivery date to return most items for a full refund, as long as they're unused and in original packaging. Electronics have a shorter 14-day window.",
  "sources": ["returns-and-cancellations.md"]
}
```

Query rewriting + chat memory work together on follow-ups:

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "what is the return window for electronics?", "conversationId": "session-xyz"}'

curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "and what about everything else?", "conversationId": "session-xyz"}'
```

### Debugging retrieval (see what the vector search actually returns)

```bash
curl "http://localhost:8080/api/knowledge-base/search?q=return%20window&topK=3"
```

### Direct REST (no LLM, for testing / integration)

```bash
curl http://localhost:8080/api/orders/ORD12345
curl http://localhost:8080/api/orders/ORD12345/status
curl -X PATCH http://localhost:8080/api/orders/ORD24680/delivery-location \
  -H "Content-Type: application/json" \
  -d '{"deliveryAddress": "22 Baker Street, Dublin, Ireland"}'
```

## Project structure

```
order-agent-boot4/
├── pom.xml
├── docker-compose.yml
└── src/main/
    ├── java/com/example/orderagent/
    │   ├── OrderAgentApplication.java
    │   ├── model/Order.java                       # JPA entity
    │   ├── repository/OrderRepository.java
    │   ├── service/OrderService.java               # business logic + rules
    │   ├── service/OrderNotFoundException.java
    │   ├── tools/OrderTools.java                   # @Tool methods called by the LLM
    │   ├── config/AgentConfig.java                 # ChatClient: system prompt, tools, memory, RAG advisor
    │   ├── config/KnowledgeBaseIngestionRunner.java # embeds + loads docs into pgvector on startup
    │   ├── controller/AgentController.java          # POST /api/agent/chat
    │   ├── controller/OrderController.java          # plain REST CRUD
    │   ├── controller/KnowledgeBaseDebugController.java # GET /api/knowledge-base/search (debug)
    │   ├── controller/GlobalExceptionHandler.java
    │   └── dto/ChatRequest.java, ChatResponse.java
    └── resources/
        ├── application.yml
        ├── data.sql                                # seed data (orders)
        └── knowledge-base/                          # RAG source documents
            ├── shipping-policy.md
            ├── returns-and-cancellations.md
            └── faq.md
```

## Spring Boot 4 / Spring AI 2.0 specifics worth knowing

- **`spring-boot-starter-webmvc`, not `spring-boot-starter-web`**: Boot 4's
  modularization renamed the web starter. The old name still resolves (deprecated,
  bundled via a "classic starter") but the new name is used here.
- **`spring-boot-starter-jdbc` is listed explicitly** alongside `data-jpa`, since it's
  what backs `JdbcTemplate` (used by the knowledge-base ingestion runner) under the
  new modular starter layout.
- **Jackson 3 is mandatory** in Boot 4. This project doesn't touch Jackson directly —
  Spring's default JSON handling of the `record` DTOs just works — but if you add
  custom `ObjectMapper` code elsewhere, note the package moved from
  `com.fasterxml.jackson` to `tools.jackson`.
- **Chat memory conversation ID is now mandatory** in Spring AI 2.0:
  `MessageChatMemoryAdvisor` throws if `ChatMemory.CONVERSATION_ID` isn't supplied on
  every call (the old implicit `"default"` constant was removed). `AgentController`
  already handles this — it defaults to `"default"` itself before passing it through.
- **`gemma4:31b` context window**: Ollama defaults Gemma 4 models to a 4K context
  window regardless of the model's real capacity. `application.yml` sets
  `spring.ai.ollama.chat.options.num-ctx: 8192` explicitly — bump it further if your
  conversations + retrieved RAG context are running long.

## Notes & things to harden before production

- **Business rule implemented**: `changeDeliveryLocation` refuses to update an order
  whose status is `DELIVERED` or `CANCELLED`.
- **Conversation memory** uses Spring AI's default `InMemoryChatMemoryRepository` —
  per-JVM-instance only and cleared on restart. Swap in a JDBC-backed
  `ChatMemoryRepository` bean for persistence across restarts / multiple instances;
  the `chatMemory()` bean in `AgentConfig` doesn't need to change either way.
- **No auth** is included — add Spring Security (e.g. verify the caller owns the order
  before letting the agent read/modify it) before exposing this publicly.
- **Model reliability**: local models occasionally misfire on tool arguments. Add
  integration tests around `OrderTools` directly, and consider logging/asserting
  tool-call arguments in a staging environment before trusting them in production.
- `ddl-auto: update` is convenient for development; use a migration tool
  (Flyway/Liquibase) for production schema management.
- **RAG tuning**: `topK` and `similarityThreshold` for `VectorStoreDocumentRetriever`
  are set in `AgentConfig.retrievalAugmentationAdvisor()` (currently topK=4,
  threshold=0.55). Use the `/api/knowledge-base/search` debug endpoint to see what a
  query actually retrieves and adjust from there.
- **`allowEmptyContext(true)` is deliberate**: this agent mixes RAG (policy questions)
  and tool calling (order questions) on one endpoint. If you split them into separate
  endpoints, switch a knowledge-base-only endpoint back to `allowEmptyContext(false)`
  (the Spring AI default) so it explicitly refuses instead of guessing.
- **Re-ingesting the knowledge base**: the ingestion runner only loads documents when
  `vector_store` is empty. To re-ingest after editing the markdown files, either
  truncate that table (`TRUNCATE vector_store;`) or drop/recreate the Postgres volume.
- **Query rewriting cost**: `RewriteQueryTransformer` adds one extra LLM round trip per
  message. This improves retrieval quality for follow-up questions but adds latency —
  remove it from the advisor chain in `AgentConfig` if that trade-off isn't worth it.
