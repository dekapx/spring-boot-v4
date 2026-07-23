# Order Agent — Spring Boot + PostgreSQL + Ollama

A REST API where an AI "order agent" answers natural-language questions like
**"Where is my order?"** by:

1. Sending the user's message to a local LLM (via [Ollama](https://ollama.com)) to extract intent + order number.
2. Fetching the real order record from PostgreSQL.
3. Asking the LLM to compose a friendly natural-language reply grounded in that verified data
   (with a deterministic fallback reply if Ollama is down, so the API never silently fails).

## Stack

- Java 17, Spring Boot 3.3
- Spring Web + WebFlux (`WebClient` used only to call Ollama's HTTP API — the REST API itself is standard blocking Spring MVC)
- Spring Data JPA + PostgreSQL
- Ollama running locally (any local model, e.g. `llama3.1`, `mistral`, `qwen2.5`)

## Project layout

```
src/main/java/com/example/orderagent/
├── OrderAgentApplication.java
├── model/Order.java                 # JPA entity
├── repository/OrderRepository.java
├── service/
│   ├── OllamaService.java           # talks to Ollama /api/generate
│   ├── OrderAgentService.java       # orchestrates intent -> DB -> reply
│   └── OrderService.java            # plain CRUD
├── controller/
│   ├── OrderAgentController.java    # POST /api/agent/chat  <-- the agent
│   └── OrderController.java         # CRUD REST endpoints
├── dto/                             # request/response records
└── config/
    ├── OllamaConfig.java            # WebClient bean
    └── GlobalExceptionHandler.java
src/main/resources/
├── application.yml
└── data.sql                         # seed data (5 sample orders)
docker-compose.yml                   # postgres + ollama + app
Dockerfile
```

## Running it

### Option A — Docker Compose (easiest)

```bash
docker compose up -d --build
# first time only: pull a model into the ollama container
docker exec -it order-agent-ollama ollama pull llama3.1
```

The app starts on `http://localhost:8080`.

### Option B — Run locally

1. **Postgres**: start any Postgres 14+ instance with a database named `orderdb`
   (or set `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` env vars to match yours).
2. **Ollama**: install from https://ollama.com, then:
   ```bash
   ollama serve            # runs on localhost:11434 by default
   ollama pull llama3.1
   ```
3. **App**:
   ```bash
   mvn spring-boot:run
   ```

Spring Boot creates the `orders` table automatically (`ddl-auto: update`) and seeds
5 sample orders from `data.sql` on first startup.

## Configuration (env vars, all optional — sensible defaults included)

| Variable          | Default                  | Purpose                          |
|-------------------|---------------------------|-----------------------------------|
| `DB_HOST`         | `localhost`               | Postgres host                     |
| `DB_PORT`         | `5432`                    | Postgres port                     |
| `DB_NAME`         | `orderdb`                 | Postgres database name            |
| `DB_USER`         | `postgres`                | Postgres user                     |
| `DB_PASSWORD`     | `postgres`                | Postgres password                 |
| `OLLAMA_BASE_URL` | `http://localhost:11434`  | Ollama server URL                 |
| `OLLAMA_MODEL`    | `llama3.1`                | Any model you've pulled in Ollama |

## Using the Order Agent

### Ask "where is my order?" in plain English

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hey, where is my order ORD-1023?"}'
```

Response:
```json
{
  "reply": "Your order ORD-1023 is out for delivery and currently at the local delivery facility in Dublin. It's expected to arrive by 2026-07-22, tracked under TRK-9023 with UPS.",
  "intent": "ORDER_STATUS",
  "order": {
    "id": 2,
    "orderNumber": "ORD-1023",
    "customerName": "Bob Smith",
    "itemName": "Mechanical Keyboard",
    "status": "OUT_FOR_DELIVERY",
    "...": "..."
  }
}
```

It also works with just the number, no "ORD-" prefix:
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "wheres my order 1023"}'
```

If no order number is given, the agent asks for one:
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "where is my order?"}'
```

If asked for all of a customer's orders:
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "show me all orders for Bob Smith"}'
```

### Plain CRUD endpoints (no LLM involved)

```bash
GET    /api/orders                     # list all
GET    /api/orders/{id}                # by primary key
GET    /api/orders/number/{orderNumber}# by order number, e.g. ORD-1023
POST   /api/orders                     # create
PUT    /api/orders/{id}                # update
DELETE /api/orders/{id}                # delete
```

## Swagger UI
http://localhost:8081/order-agent/swagger-ui/index.html


## How the agent decides what to do

`OrderAgentService.handleUserMessage()`:

1. Sends the message to `OllamaService.extractIntent()`, prompting the model (in **JSON mode**)
   to classify intent (`ORDER_STATUS` / `ORDER_LIST` / `GENERAL`) and pull out an order number / customer name.
2. Falls back to a regex (`ORD-1023`, `#1023`, `1023`, etc.) if the LLM misses the order number or is unreachable —
   so the agent still works even without a perfect LLM extraction.
3. Looks the order up in Postgres via `OrderRepository`.
4. Passes the **real DB record** (not anything the LLM invented) back to Ollama to phrase a natural reply.
5. If Ollama fails entirely, falls back to a deterministic, template-based reply built directly from the DB row —
   so the REST API always returns a useful answer even if the LLM is down.

## Notes / things you may want to extend

- Swap the regex/JSON-mode intent extraction for Ollama's native **tool-calling** API
  (`/api/chat` with a `tools` array) if you're on a tool-calling-capable model — this skill's
  two-call design (extract → fetch → compose) makes that swap straightforward.
- Add Spring Security if this needs to be customer-authenticated (right now any caller can query any order number).
- Add a conversation/session id if you want multi-turn context (e.g. "actually, what about my other order?").
