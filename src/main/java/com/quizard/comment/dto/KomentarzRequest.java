package com.quizard.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KomentarzRequest {
    @NotBlank
    private String tresc;
}
