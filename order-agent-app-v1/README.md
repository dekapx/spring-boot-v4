# Order Agent — Spring Boot + PostgreSQL + Ollama

## Swagger UI
http://localhost:8081/order-agent/swagger-ui/index.html

```
- http://localhost:8081/order-agent/api/agent/chat
```

```json
{
  "message": "where is my order ORD12345?"
}

{
  "answer": "Your order ORD24680 is currently being processed at our Dublin warehouse. It's expected to be delivered by August 18, 2026, via An Post."
}
```

```json
{
  "message": "can you give me the full details for ORD12345"
}
```

```json
{
  "message": "please change delivery address for ORD24680 to 22 Baker Street, Dublin"
}
```