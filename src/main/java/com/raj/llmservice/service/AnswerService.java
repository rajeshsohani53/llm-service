package com.raj.llmservice.service;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {

    private final ChatModel chatModel;

    public AnswerService(@Value("${groq.api.key}") String apiKey) {
        // Groq is OpenAI-compatible → reuse the OpenAI client, point it at Groq's URL
        this.chatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(apiKey)
                .modelName("openai/gpt-oss-20b")   // current Groq model (not deprecated)
                .build();
    }

    public String answer(String question, List<String> contextChunks) {
        // join the retrieved chunks into one context block
        String context = String.join("\n\n", contextChunks);

        // THE RAG PROMPT — inject context, force the model to answer only from it
        String prompt = """
                Answer the question using ONLY the context below.
                If the answer is not in the context, say you don't know.

                Context:
                %s

                Question: %s
                """.formatted(context, question);

        return chatModel.chat(prompt);
    }
}