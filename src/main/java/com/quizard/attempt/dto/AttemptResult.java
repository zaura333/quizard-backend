package com.quizard.attempt.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AttemptResult {
    private Long quizId;
    private String quizType;

    // dla quizów z oceną
    private Integer totalPoints;
    private Integer maxPoints;
    private Double percentScore;
    private List<QuestionResult> questionResults;

    // dla quizu osobowości
    private String personalityResult;
    private Map<String, Integer> personalityVotes;

    @Data
    @Builder
    public static class QuestionResult {
        private Long questionId;
        private String pytanieType;
        private boolean correct;
        private int pointsEarned;
        private int maxPoints;
        private String correctAnswer;
        private String givenAnswer;
    }
}
