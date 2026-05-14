package com.quizard.attempt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AttemptRequest {

    @NotNull
    private List<AnswerDto> answers;

    private Long startedAtEpochMs; // do walidacji limitu czasu po stronie backendu

    @Data
    public static class AnswerDto {
        @NotNull
        private Long questionId;
        private String answer; // format zależy od typu pytania
    }
}
