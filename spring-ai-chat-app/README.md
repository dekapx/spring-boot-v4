# Spring AI Chat App

A Spring Boot REST chat application with a React browser UI and a local Ollama model.

## Technology stack

- Java 21
- Spring Boot 3.5.16
- Spring AI 1.1.8
- Maven
- Ollama: `qwen3-coder:30b`
- React 18 (loaded from public CDNs; no Node.js build is required)

## Prerequisites

1. JDK 21
2. Maven 3.6.3+
3. Ollama installed and running
4. Sufficient RAM/VRAM for `qwen3-coder:30b` (a 30B model is resource-intensive)

## 1. Download and start the model

```bash
ollama pull qwen3-coder:30b
ollama serve
```

If Ollama is already running as a desktop service, you do not need to run `ollama serve` again.

Verify the model is available:

```bash
ollama list
```

## 2. Run the application

From the project root:

```bash
mvn clean spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080).

To build an executable JAR:

```bash
mvn clean package
java -jar target/spring-ai-chat-app-0.0.1-SNAPSHOT.jar
```

## 3. REST API

### Request

`POST http://localhost:8080/api/chat`

```json
{
  "message": "Explain Java virtual threads",
  "history": [
    { "role": "user", "content": "I am learning modern Java." },
    { "role": "assistant", "content": "I can help with modern Java topics." }
  ]
}
```

### Response

```json
{
  "reply": "Java virtual threads are lightweight threads..."
}
```

`history` is optional. The browser sends the preceding user/assistant turns with each request so the model can answer in conversational context. This starter keeps that history in browser memory only; refreshing the page clears it.

Test with curl:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"What is Spring AI?","history":[]}'
```

## Configuration

`src/main/resources/application.yml` contains the default Ollama settings. Override them with environment variables:

```bash
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=qwen3-coder:30b
```

The application defaults to `http://localhost:11434` and `qwen3-coder:30b`.

## Notes

- The UI is served by Spring Boot from `src/main/resources/static`.
- React and Babel are loaded from unpkg CDN, so the browser needs internet access for those assets. For a fully offline deployment, vendor React and Babel locally or replace the CDN scripts with a bundled frontend.
- The API is intentionally unauthenticated for local development. Add authentication, rate limiting, and request controls before exposing it outside your machine.
- The API returns a complete response (not token-by-token streaming).
