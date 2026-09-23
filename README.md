# LLM Service

A standalone Spring Boot **microservice** with one job: taking a question plus the retrieved document chunks and producing a **grounded answer** with an LLM.

It is part of a **Retrieval-Augmented Generation (RAG)** system built from scratch in Java with Spring Boot and LangChain4j, split into five independent microservices.

---

## Role in the System

This is **service #5**, the final step of the RAG pipeline.

| Port | Service | Responsibility |
|------|---------|----------------|
| 8080 | API Gateway / Orchestrator | Single entry point; coordinates the flow |
| 8081 | Document Service | Raw document → clean chunks |
| 8082 | Embedding Service | Text → vector |
| 8083 | Retrieval Service | Stores vectors; query vector → most relevant chunks |
| **8084** | **LLM / Answer Service** | **Chunks + question → final answer** ← *this service* |

---

## How It Works

1. Receives a question and a list of context chunks (the top matches from the Retrieval Service).
2. Joins the chunks into a single context block.
3. Builds a RAG prompt that tells the model to answer **only** from that context, and to say it doesn't know if the answer isn't there. This keeps answers grounded in the document and reduces hallucination.
4. Sends the prompt to the LLM and returns the answer.

The model is served by **Groq**. Groq's API is OpenAI-compatible, so the service reuses LangChain4j's `OpenAiChatModel` and just points its base URL at Groq (`https://api.groq.com/openai/v1`), using the `openai/gpt-oss-20b` model.

---

## Tech Stack

- **Java 17**
- **Spring Boot 4.1.1** (Spring Web MVC)
- **LangChain4j 1.19.0** (`langchain4j-open-ai`)
- **Groq** (`openai/gpt-oss-20b`)
- **Maven**

---

## API

### `POST /api/llm/answer`

```bash
curl -X POST http://localhost:8084/api/llm/answer \
     -H "Content-Type: application/json" \
     -d '{
           "question": "What port does the gateway run on?",
           "context": ["The API Gateway runs on port 8080.", "Each service runs as its own process."]
         }'
```

```json
{
  "answer": "The gateway runs on port 8080."
}
```

---

## Running Locally

**Prerequisites:** JDK 17+, a Groq API key.

The API key is read from an **environment variable**. It is never stored in the code or committed to git.

```bash
# Linux / macOS
export GROQ_API_KEY=your-key-here

# Windows PowerShell
$env:GROQ_API_KEY="your-key-here"

./mvnw spring-boot:run
```

The service starts on **http://localhost:8084**.

### Configuration

`src/main/resources/application.properties`:

```properties
server.port=8084
groq.api.key=${GROQ_API_KEY}
```

---

## Project Structure

```
llm-service/
├── src/main/java/com/raj/llmservice/
│   ├── LlmServiceApplication.java                     # entry point
│   ├── controller/AnswerController/AnswerController.java  # REST endpoint
│   └── service/AnswerService.java                     # RAG prompt + Groq chat model
├── src/main/resources/application.properties
└── pom.xml
```
