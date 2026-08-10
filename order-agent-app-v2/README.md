# Order Agent — Spring Boot + Postgres + Ollama

## How it works

```
User message ("where is my order ORD12345?")
        │
        ▼
POST /api/agent/chat
        │
        ▼
ChatClient (Spring AI) ── system prompt + conversation memory
        │
        ▼
Ollama model (llama3.1) decides: "call getOrderStatus(orderNumber='ORD12345')"
        │
        ▼
OrderTools.getOrderStatus() → OrderService → OrderRepository → Postgres
        │
        ▼
Tool result text is fed back to the model
        │
        ▼
Model composes a friendly natural-language reply → returned as JSON
```

The agent supports three capabilities out of the box, each backed by a `@Tool`-annotated
method in `OrderTools.java`:

| Capability              | Tool method                                   |
|--------------------------|-----------------------------------------------|
| Find order details       | `getOrderDetails(orderNumber)`                |
| Find order status        | `getOrderStatus(orderNumber)`                 |
| Change delivery location | `changeDeliveryLocation(orderNumber, address)`|

Adding a new capability is just: write a new method in `OrderTools`, annotate it with
`@Tool`, describe its parameters with `@ToolParam` — no prompt engineering or intent
classification code required, Spring AI + Ollama handle routing.

## Running

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`, creates/updates the `orders` table
(`ddl-auto: update`), and seeds three sample orders from `data.sql`
(`ORD12345`, `ORD67890`, `ORD24680`).

## Using the agent

### Natural language (the agent)

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "where is my order ORD12345?"}'
```

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "can you give me the full details for ORD12345"}'
```

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "please change delivery address for ORD24680 to 22 Baker Street, Dublin"}'
```

Pass a `conversationId` to keep separate chat threads with memory (e.g. one per
customer session):

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "where is my order?", "conversationId": "session-abc"}'

curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "it is ORD12345", "conversationId": "session-abc"}'
```
The model will ask for the order number in the first call (since none was given) and
use conversation memory to remember it in the second.

### Direct REST (no LLM, for testing / integration)

```bash
curl http://localhost:8080/api/orders/ORD12345
curl http://localhost:8080/api/orders/ORD12345/status
curl -X PATCH http://localhost:8080/api/orders/ORD24680/delivery-location \
  -H "Content-Type: application/json" \
  -d '{"deliveryAddress": "22 Baker Street, Dublin, Ireland"}'
```

```json
{
  "message": "where is my order ORD12345?",
  "conversationId": "1"
}
```

```json
{
  "message": "can you give me the full details for ORD12345",
  "conversationId": "1"
}
```

```json
{
  "message": "please change delivery address for ORD24680 to 22 Baker Street, Dublin",
  "conversationId": "1"
}
```
