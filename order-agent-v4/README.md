# order-agent-app

A Spring Boot **Order Agent** REST application: a routing, tool-calling, RAG-grounded,
self-critiquing AI agent (via [Spring AI](https://docs.spring.io/spring-ai/reference/) +
[Ollama](https://ollama.com), model `qwen3-coder:30b`) that can find order details, check
order status, change a delivery location, and semantically search a product catalog — plus
plain REST endpoints for direct order/product access.

## Tech stack

| Concern            | Choice                                   |
|---------------------|-------------------------------------------|
| Language / JDK      | Java 21                                   |
| Framework           | Spring Boot 3.5.10                        |
| Build               | Maven                                     |
| Database            | PostgreSQL (+ `pgvector` extension)       |
| Migrations          | Flyway                                    |
| LLM runtime         | Ollama, chat model `qwen3-coder:30b`      |
| Embeddings          | Ollama, `nomic-embed-text` (768-dim)      |
| Agent framework     | Spring AI 1.0.0 (`ChatClient`, tool calling, advisors, `ChatMemory`) |
| groupId / artifactId| `com.dekapx.apps` / `order-agent-app`     |

## The 7 AI capabilities, and where they live

| # | Capability | Implementation |
|---|------------|-----------------|
| 1 | **Retrieval-Augmented Generation** | `RecursiveRetrievalAdvisor` (policy-filtered) plugged into `orderAgentChatClient` / `policyChatClient` in `config/ChatClientConfig.java`. Retrieved chunks are injected as context before the model answers. |
| 2 | **Vector Store Integration** | Single shared **pgvector** table (`vector_store`), configured in `application.yml`, storing both policy-doc and product-catalog embeddings side by side, distinguished by a `docType` metadata field. |
| 3 | **ETL Document Pipeline** | `etl/DocumentEtlPipeline.java` — an explicit Extract → Transform → Load pipeline (`DocumentReader` → `DocumentTransformer`(s) → `DocumentWriter`). `etl/IngestionOrchestrator.java` runs one pipeline for the markdown policy docs and one for the product catalog on startup. |
| 4 | **Semantic Product Search** | `service/ProductSearchService.java` embeds the query, searches the vector store filtered to `docType == 'product'`, and hydrates full `Product` rows. Exposed via `GET /api/products/search` and the `semanticProductSearch` agent tool. |
| 5 | **Recursive Advisors** | `advisor/RecursiveRetrievalAdvisor.java` implements Spring AI's `CallAdvisor` SPI and recursively (a) decomposes compound questions into sub-questions and (b) broadens weak/empty searches — instead of one flat similarity search. |
| 6 | **Routing Workflows** | `routing/QueryRouter.java` classifies each message into `ORDER_LOOKUP` / `PRODUCT_SEARCH` / `POLICY_QUESTION` / `GENERAL` / `ESCALATION`; `service/AgentService.java` dispatches to a differently-configured `ChatClient` per route. |
| 7 | **Evaluator-Optimizer Pattern** | `evaluator/EvaluatorOptimizerService.java` scores each generated response against a rubric and, if it falls short, re-generates with the evaluator's feedback folded in — bounded by a max-iteration budget. |

## Architecture

```
                     ┌──────────────────────────┐
  REST clients ─────▶│ AgentController           │ POST /api/agent/chat
                     │ OrderController            │ /api/orders/**
                     │ ProductController           │ GET  /api/products/search
                     └──────────┬────────────────┘
                                │
                     ┌──────────▼────────────────┐
                     │   AgentService              │  (6) Routing Workflow dispatch
                     └──────────┬────────────────┘
             ┌──────────────────┼────────────────────┐
     ORDER_LOOKUP          PRODUCT_SEARCH        POLICY_QUESTION / GENERAL
             │                  │                       │
   orderAgentChatClient   productChatClient       policyChatClient
   (tools + memory +     (semanticProductSearch   (RAG-only, no tools)
    RecursiveRetrieval     tool)
    Advisor)
             │                  │                       │
             └──────────┬───────┴───────────────────────┘
                         │
              (7) EvaluatorOptimizerService
              generate → evaluate → (revise if needed)
                         │
             ┌───────────▼────────────┐        ┌────────────────────┐
             │ Spring AI ChatClient    │◀──────▶│ Ollama               │
             │ (1)(5) RAG + Recursive  │        │ qwen3-coder:30b      │
             │ Advisor                 │        │ nomic-embed-text     │
             └───────────┬────────────┘        └────────────────────┘
             ┌───────────▼────────────┐
             │ (2) pgvector store      │  docType=policy | docType=product
             │ (4) ProductSearchService│
             │ OrderService/Repo       │
             └───────────┬────────────┘
             ┌───────────▲────────────┐
             │ (3) IngestionOrchestrator + DocumentEtlPipeline
             │     policy docs (*.md) ──┐
             │     product catalog (JPA)┘  Extract → Transform → Load
             └─────────────────────────┘
```

### How a request flows

1. **Routing Workflow** (`QueryRouter`) classifies the message (a cheap, tool-free LLM call).
2. Based on the route, `AgentService` builds a **generator function** backed by the
   route-appropriate `ChatClient`:
   - `ORDER_LOOKUP` → `orderAgentChatClient`: order + product tools, conversation memory, and
     the **Recursive Advisor** for policy grounding.
   - `PRODUCT_SEARCH` → `productChatClient`: the **Semantic Product Search** tool only.
   - `POLICY_QUESTION` → `policyChatClient`: **RAG-only**, no tools, grounded purely in
     retrieved policy chunks.
   - `GENERAL` → answered directly, no evaluator loop (small talk doesn't need one).
   - `ESCALATION` → a fixed handoff message, no LLM call at all.
3. The generator is handed to `EvaluatorOptimizerService.optimize(...)`, which calls it,
   scores the result against a rubric (grounding, relevance, tone), and — if it scores below
   the acceptance threshold — re-invokes the generator with the evaluator's feedback appended,
   up to a small iteration budget.
4. Whichever `ChatClient` is used, the **Recursive Advisor** intercepts the call, recursively
   retrieves (decomposing compound questions, broadening weak searches) against the
   **pgvector store**, injects the merged context, and only then lets the model respond.

## Prerequisites

1. **Java 21**, **Maven 3.9+**
2. **Docker** (for Postgres + Ollama), or your own local install of each
3. Pull the two Ollama models used by this app:
   ```bash
   ollama pull qwen3-coder:30b
   ollama pull nomic-embed-text
   ```

## Running locally

```bash
# 1. Start Postgres (with pgvector) and Ollama
docker compose up -d

# 2. Pull models into the running Ollama container (first time only)
docker exec -it order-agent-ollama ollama pull qwen3-coder:30b
docker exec -it order-agent-ollama ollama pull nomic-embed-text

# 3. Build & run the app (Flyway creates schema + seeds sample orders/products;
#    the ETL pipelines then embed the policy docs and product catalog on first boot)
mvn spring-boot:run
```

> This project doesn't ship a Maven wrapper — use your local Maven 3.9+ install (`mvn -v`
> to check), or generate one yourself with `mvn -N wrapper:wrapper -Dmaven=3.9.9`.

### Configuration (env vars, all optional — sensible defaults included)

| Variable               | Default                                       |
|-------------------------|------------------------------------------------|
| `DB_URL`                | `jdbc:postgresql://localhost:5432/order_agent_db` |
| `DB_USERNAME`           | `order_agent`                                  |
| `DB_PASSWORD`           | `order_agent`                                  |
| `OLLAMA_BASE_URL`       | `http://localhost:11434`                       |
| `OLLAMA_CHAT_MODEL`     | `qwen3-coder:30b`                              |
| `OLLAMA_EMBEDDING_MODEL`| `nomic-embed-text`                             |
| `SERVER_PORT`           | `8080`                                         |

## API

### Agent (natural language, routed)

```
POST /api/agent/chat
Content-Type: application/json

{ "message": "Where is my order ORD-1002?", "conversationId": null }
```

Response:
```json
{
  "reply": "Order ORD-1002 is currently OUT_FOR_DELIVERY, out for delivery near Dublin, IE via UPS...",
  "conversationId": "3d1e2f4a-...-....",
  "route": "ORDER_LOOKUP"
}
```

Reuse `conversationId` on follow-up calls to continue the same conversation.

Example prompts to exercise each capability:
- `"Can you give me the full details for order ORD-1001?"` → **ORDER_LOOKUP**, tool calling
- `"Why can't I change the address on ORD-1004?"` → **POLICY_QUESTION**, RAG
- `"I want to cancel ORD-1002 AND also know if I can change its delivery address"` →
  **ORDER_LOOKUP**, exercises the **Recursive Advisor**'s question-decomposition path
- `"I need something to help with wrist strain from typing all day, budget around $50"` →
  **PRODUCT_SEARCH**, **Semantic Product Search**
- `"This is the third time my order has been late, I want to speak to a manager"` →
  **ESCALATION**

### Direct REST (no LLM involved)

| Method | Path                                  | Description                          |
|--------|------------------------------------------|---------------------------------------|
| POST   | `/api/orders`                          | Create an order                       |
| GET    | `/api/orders`                          | List all orders                       |
| GET    | `/api/orders/{orderNumber}`            | Get full order details                |
| GET    | `/api/orders/{orderNumber}/status`     | Get just the status                   |
| GET    | `/api/orders?customerName=...`         | Search orders by customer name        |
| PATCH  | `/api/orders/delivery-location`        | Change delivery address               |
| GET    | `/api/products/search?q=...&topK=5`    | Semantic product search (embeddings)  |

## Project layout

```
src/main/java/com/dekapx/apps/orderagent/
├── OrderAgentAppApplication.java
├── advisor/RecursiveRetrievalAdvisor.java   # (5) Recursive Advisor
├── config/ChatClientConfig.java              # per-route ChatClient beans, tools, memory
├── controller/                               # OrderController, ProductController, AgentController
├── dto/                                       # OrderDtos, ProductDtos
├── entity/                                    # Order, Product
├── etl/
│   ├── DocumentEtlPipeline.java              # (3) generic Extract-Transform-Load pipeline
│   ├── IngestionOrchestrator.java            # runs the policy + product pipelines on startup
│   ├── MetadataTaggingTransformer.java       # ETL "transform" stage
│   └── ProductDocumentReader.java            # ETL "extract" stage for the product catalog
├── evaluator/
│   ├── EvaluationResult.java
│   └── EvaluatorOptimizerService.java        # (7) Evaluator-Optimizer Pattern
├── routing/
│   ├── QueryRoute.java
│   └── QueryRouter.java                      # (6) Routing Workflow
├── repository/                                # OrderRepository, ProductRepository
├── service/
│   ├── OrderService.java, OrderMapper.java
│   ├── ProductSearchService.java             # (4) Semantic Product Search
│   └── AgentService.java                     # orchestrates routing + evaluator-optimizer
└── tools/OrderTools.java                     # @Tool functions callable by the LLM
src/main/resources/
├── application.yml                            # (2) pgvector Vector Store Integration config
├── db/migration/                              # Flyway: orders/products tables, seed data
└── docs/                                       # RAG knowledge base (markdown policy docs)
```

## Notes & caveats

- **Advisor SPI**: `RecursiveRetrievalAdvisor` implements Spring AI's `CallAdvisor` interface
  (`adviseCall(ChatClientRequest, CallAdvisorChain)`). This SPI has shifted across Spring AI
  milestones; this project targets the 1.0.0 GA shape. If you pin a different Spring AI
  version, double-check `ChatClientRequest#mutate()` and `CallAdvisorChain#nextCall(...)`
  still match, and adjust if the API has moved.
- **Sandbox build note**: this project was generated without Maven Central access in the
  authoring environment, so it has not been `mvn compile`'d end-to-end here — every import
  and API call was hand-checked against the pinned Spring AI 1.0.0 / Spring Boot 3.5 surface,
  but please run `mvn spring-boot:run` locally as your first real verification step.
- **Evaluator-Optimizer cost**: each optimizer iteration is an extra LLM round-trip. The
  iteration budgets are deliberately small (2 for order lookups, 3 default elsewhere) to keep
  latency reasonable with a local Ollama model; tune `EvaluatorOptimizerService`'s
  `DEFAULT_MAX_ITERATIONS` / `ACCEPTANCE_SCORE` to taste.
- **Chat memory + evaluator revisions**: when the optimizer re-invokes the `ORDER_LOOKUP`
  generator with feedback appended, that revised prompt is also recorded in `ChatMemory`
  (since it reuses the same `conversationId`). This is fine for a demo but worth knowing if
  you inspect conversation history.
