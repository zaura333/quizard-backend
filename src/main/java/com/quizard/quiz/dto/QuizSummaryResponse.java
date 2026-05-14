package com.quizard.quiz.dto;

import com.quizard.quiz.model.*;
import com.quizard.user.dto.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String quizType;
    private UserResponse author;
    private int likesCount;
    private int questionsCount;
    private Integer limitCzasuSekundy;
    private List<String> poziomy;         // Ranking — poziomy tierlisty (np. S/A/B/C/D)
    private List<String> mozliweWyniki;   // QuizOsobowosci — możliwe wyniki osobowości
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QuizSummaryResponse from(Quiz quiz) {
        QuizSummaryResponse r = new QuizSummaryResponse();
        r.setId(quiz.getId());
        r.setTitle(quiz.getTitle());
        r.setDescription(quiz.getDescription());
        r.setCategory(quiz.getCategory().name());
        r.setStatus(quiz.getStatus().name());
        r.setQuizType(quiz.getClass().getSimpleName());
        r.setAuthor(quiz.getAuthor() != null ? UserResponse.from(quiz.getAuthor()) : null);
        r.setLikesCount(quiz.getLikedBy().size());
        r.setQuestionsCount(quiz.getPytania().size());
        r.setCreatedAt(quiz.getCreatedAt());
        r.setUpdatedAt(quiz.getUpdatedAt());
        if (quiz instanceof OgraniczonymCzasem oc) {
            r.setLimitCzasuSekundy(oc.getLimitCzasuSekundy());
        }
        if (quiz instanceof Ranking rk) {
            r.setPoziomy(rk.getPoziomy());
        }
        if (quiz instanceof QuizOsobowosci qo) {
            r.setMozliweWyniki(qo.getMozliweWyniki());
        }
        return r;
    }
}
