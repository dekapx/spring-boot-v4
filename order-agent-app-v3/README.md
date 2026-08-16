# order-agent-app

A Spring Boot **Order Agent** REST application: a tool-calling, RAG-grounded AI agent (via
[Spring AI](https://docs.spring.io/spring-ai/reference/) + [Ollama](https://ollama.com),
model `qwen3-coder:30b`) that can find order details, check order status, and change a
delivery location — plus plain REST endpoints for direct order access.

## Swagger UI
http://localhost:8081/order-agent/swagger-ui/index.html

```
- http://localhost:8081/order-agent/api/agent/chat
```

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
| Agent framework     | Spring AI 1.0.0 (`ChatClient`, tool calling, `QuestionAnswerAdvisor` RAG, `ChatMemory`) |
| groupId / artifactId| `com.dekapx.apps` / `order-agent-app`     |

## Architecture

```
                     ┌──────────────────────┐
  REST clients ─────▶│  AgentController      │  POST /api/agent/chat
                     │  OrderController      │  /api/orders/**
                     └──────────┬────────────┘
                                │
                     ┌──────────▼────────────┐
                     │     AgentService       │  builds prompt + RAG advisor
                     └──────────┬────────────┘
                     ┌──────────▼────────────┐        ┌───────────────────┐
                     │  Spring AI ChatClient  │◀──────▶│  Ollama            │
                     │  + OrderTools (fn call)│        │  qwen3-coder:30b   │
                     │  + ChatMemory          │        │  nomic-embed-text  │
                     └──────────┬────────────┘        └───────────────────┘
                     ┌──────────▼────────────┐
                     │  PgVectorStore (RAG)   │  policy docs in /docs/*.md
                     │  OrderService/Repo     │  orders table (Postgres)
                     └────────────────────────┘
```

- **Tool calling / agentic actions**: `OrderTools` exposes `findOrderDetails`,
  `findOrderStatus`, `changeDeliveryLocation`, and `findOrdersByCustomerName` as
  `@Tool`-annotated methods. The model decides when to call them based on the user's message.
- **RAG**: on startup, `DocumentIngestionService` embeds the markdown docs under
  `src/main/resources/docs/` (delivery policy, carrier/tracking info, cancellation policy)
  into the Postgres `vector_store` table. Each chat request runs a `QuestionAnswerAdvisor`
  similarity search against that store so the model can ground policy answers in real content.
- **Memory**: `ChatMemory` (in-memory, 20-message window) keyed by `conversationId` lets the
  agent hold multi-turn conversations ("what's the status of ORD-1002?" → "now change its
  delivery address to ...").

## Prerequisites

1. **Java 21**, **Maven 3.9+**
2. **Docker** (for Postgres + Ollama), or your own local installation of each
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

# 3. Build & run the app (Flyway will create schema + seed sample orders automatically)
mvn spring-boot:run
```

> This project doesn't ship a Maven wrapper — use your local Maven 3.9+ install (`mvn -v`
> to check), or generate one yourself with `mvn -N wrapper:wrapper -Dmaven=3.9.9`.

The app starts on `http://localhost:8080`.

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

### Agent (natural language)

```
POST /api/agent/chat
Content-Type: application/json

{
  "message": "Where is my order ORD-1002?",
  "conversationId": null
}
```

Response:
```json
{
  "reply": "Order ORD-1002 is currently OUT_FOR_DELIVERY, out for delivery near Dublin, IE via UPS...",
  "conversationId": "3d1e2f4a-...-...."
}
```

Reuse the returned `conversationId` on subsequent calls to continue the same conversation, e.g.:
```json
{ "message": "Please change the delivery address to 10 Downing Street, London, UK",
  "conversationId": "3d1e2f4a-...-...." }
```

Other example prompts to try:
- "Can you give me the full details for order ORD-1001?"
- "Why can't I change the address on ORD-1004?" (tests RAG grounding against delivered-order policy)
- "Find orders for Alice Johnson"
- "I want to cancel ORD-1002, is that possible?" (tests RAG-grounded policy explanation)

### Direct REST (no LLM involved)

| Method | Path                                  | Description                          |
|--------|----------------------------------------|---------------------------------------|
| POST   | `/api/orders`                          | Create an order                       |
| GET    | `/api/orders`                          | List all orders                       |
| GET    | `/api/orders/{orderNumber}`            | Get full order details                |
| GET    | `/api/orders/{orderNumber}/status`     | Get just the status                   |
| GET    | `/api/orders?customerName=...`         | Search orders by customer name        |
| PATCH  | `/api/orders/delivery-location`        | Change delivery address               |

## Project layout

```
src/main/java/com/dekapx/apps/orderagent/
├── OrderAgentAppApplication.java
├── config/ChatClientConfig.java        # ChatClient bean, tools + memory + system prompt
├── controller/                         # OrderController, AgentController, error handler
├── dto/OrderDtos.java
├── entity/Order.java
├── repository/OrderRepository.java
├── service/
│   ├── OrderService.java
│   ├── OrderMapper.java
│   ├── AgentService.java               # RAG (QuestionAnswerAdvisor) + chat orchestration
│   └── DocumentIngestionService.java   # loads /docs/*.md into pgvector on startup
└── tools/OrderTools.java               # @Tool functions callable by the LLM
src/main/resources/
├── application.yml
├── db/migration/                       # Flyway: orders table, pgvector extension, seed data
└── docs/                                # RAG knowledge base (markdown policy docs)
```
