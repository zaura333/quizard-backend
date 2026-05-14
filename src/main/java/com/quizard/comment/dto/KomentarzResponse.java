package com.quizard.comment.dto;

import com.quizard.comment.model.Komentarz;
import com.quizard.user.dto.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KomentarzResponse {
    private Long id;
    private String tresc;
    private UserResponse author;
    private LocalDateTime createdAt;

    public static KomentarzResponse from(Komentarz k) {
        KomentarzResponse r = new KomentarzResponse();
        r.setId(k.getId());
        r.setTresc(k.getTresc());
        r.setAuthor(k.getAuthor() != null ? UserResponse.from(k.getAuthor()) : null);
        r.setCreatedAt(k.getCreatedAt());
        return r;
    }
}
