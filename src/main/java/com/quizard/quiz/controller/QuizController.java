package com.quizard.quiz.controller;

import com.quizard.attempt.dto.AttemptRequest;
import com.quizard.attempt.dto.AttemptResult;
import com.quizard.attempt.service.AttemptService;
import com.quizard.comment.dto.KomentarzRequest;
import com.quizard.comment.dto.KomentarzResponse;
import com.quizard.comment.service.KomentarzService;
import com.quizard.quiz.dto.CreateQuizRequest;
import com.quizard.quiz.dto.QuizSummaryResponse;
import com.quizard.quiz.model.Category;
import com.quizard.quiz.model.Quiz;
import com.quizard.quiz.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final AttemptService attemptService;
    private final KomentarzService komentarzService;

    @GetMapping
    public ResponseEntity<Page<QuizSummaryResponse>> getAll(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(quizService.getPublished(category, authorId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizSummaryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getByIdAsResponse(id));
    }

    @PostMapping
    public ResponseEntity<QuizSummaryResponse> create(
            @Valid @RequestBody CreateQuizRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Quiz quiz = quizService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuizSummaryResponse.from(quiz));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizSummaryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateQuizRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Quiz quiz = quizService.update(id, request, userId);
        return ResponseEntity.ok(QuizSummaryResponse.from(quiz));
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Void> publish(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        quizService.publish(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        quizService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/attempt")
    public ResponseEntity<AttemptResult> attempt(
            @PathVariable Long id,
            @Valid @RequestBody AttemptRequest request) {
        return ResponseEntity.ok(attemptService.evaluate(id, request));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<KomentarzResponse>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(komentarzService.getByQuiz(id, PageRequest.of(page, size)));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<KomentarzResponse> addComment(
            @PathVariable Long id,
            @RequestBody KomentarzRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(KomentarzResponse.from(komentarzService.add(id, request.getTresc(), userId)));
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        komentarzService.delete(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> like(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        boolean liked = quizService.toggleLike(id, userId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        quizService.toggleLike(id, userId);
        return ResponseEntity.ok(Map.of("liked", false));
    }
}
