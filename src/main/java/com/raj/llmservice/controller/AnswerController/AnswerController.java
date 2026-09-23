package com.raj.llmservice.controller.AnswerController;


import com.raj.llmservice.service.AnswerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/llm")
@CrossOrigin(origins = "*")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    // {"question": "...", "context": ["chunk1", "chunk2", ...]}
    @PostMapping("/answer")
    public Map<String, Object> answer(@RequestBody AnswerRequest request) {
        String answer = answerService.answer(request.question(), request.context());
        return Map.of("answer", answer);
    }

    public record AnswerRequest(String question, List<String> context) {}
}