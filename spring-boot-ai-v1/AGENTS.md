# AI Agent Guidelines for spring-boot-ai-v1

## Architecture Overview
This is a Spring Boot 3.5.10 microservice integrating LangChain4j for AI chat functionality. It provides a REST API for conversational AI using OpenRouter's free OpenAI-compatible models, with fallback to local Ollama.

Key components:
- `Assistant` interface in `chat/` - defines AI chat contract
- `OpenApiController` in `controller/` - exposes `/api/v1/chat` POST endpoint
- Config classes in `config/` - wire LangChain4j beans and OpenAPI docs
- Records in `model/` - `ChatRequest` and `ChatResponse` for API payloads

## Configuration Patterns
Use `application.yml` for environment-specific settings:
- OpenAI API via OpenRouter: `app.openai.api.*` properties
- Ollama local model: `langchain4j.ollama.*` properties
- Server: port 8081, context-path `/chat-service`

Example: Custom headers in `LangChain4jConfig.java` for OpenRouter compatibility.

## API Design Patterns
- REST endpoints under `/api/v1` in controllers
- Use records for immutable request/response DTOs (e.g., `ChatRequest(String message)`)
- Inject AI services via constructor (e.g., `OpenApiController(Assistant aiAssistant)`)

## Build and Run Workflows
- Build: `./mvnw clean compile`
- Run: `./mvnw spring-boot:run` (starts on http://localhost:8081/chat-service)
- Test: `./mvnw test` (includes context load test in `ApplicationTests.java`)
- Docs: Access Swagger UI at `/chat-service/swagger-ui.html`

## Dependencies and Integrations
- LangChain4j 1.12.2 for AI abstraction
- SpringDoc OpenAPI for API documentation
- Lombok for boilerplate reduction (annotation processors configured in `pom.xml`)

When adding features:
- Create AI interfaces in `chat/` and wire via `AiServices.create()` in config
- Add REST endpoints in `controller/` with logging via `@Slf4j`
- Update OpenAPI info in `OpenApiConfig.java` for docs
