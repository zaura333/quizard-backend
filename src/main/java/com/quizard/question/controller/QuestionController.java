package com.quizard.question.controller;

import com.quizard.question.dto.CreatePytanieRequest;
import com.quizard.question.dto.PytanieResponse;
import com.quizard.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /** Publiczny — zgodnie z SecurityConfig: GET /api/quizzes/** jest permittowany */
    @GetMapping
    public ResponseEntity<List<PytanieResponse>> getAll(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.getByQuiz(quizId));
    }

    @PostMapping
    public ResponseEntity<PytanieResponse> add(
            @PathVariable Long quizId,
            @Valid @RequestBody CreatePytanieRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.add(quizId, request, userId));
    }

    @PutMapping("/{pytanieId}")
    public ResponseEntity<PytanieResponse> update(
            @PathVariable Long quizId,
            @PathVariable Long pytanieId,
            @Valid @RequestBody CreatePytanieRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(questionService.update(quizId, pytanieId, request, userId));
    }

    @DeleteMapping("/{pytanieId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long quizId,
            @PathVariable Long pytanieId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        questionService.delete(quizId, pytanieId, userId);
        return ResponseEntity.noContent().build();
    }
}
