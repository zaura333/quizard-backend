package com.quizard.quiz.dto;

import com.quizard.quiz.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuizRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Category category;

    @NotNull
    private String quizType; // TEST_WIEDZY | OSOBOWOSCI | DOPASOWANIA | UZUPELNIANIE_LUK | RANKING

    private Integer limitCzasuSekundy;

    // dla QuizOsobowosci
    private List<String> mozliweWyniki;

    // dla Ranking — null = defaultowe S/A/B/C/D
    private List<String> poziomy;
}
