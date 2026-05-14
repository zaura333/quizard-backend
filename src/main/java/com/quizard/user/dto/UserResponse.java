package com.quizard.user.dto;

import com.quizard.user.model.Role;
import com.quizard.user.model.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setUsername(user.isDeleted() ? "[usunięty]" : user.getUsername());
        r.setEmail(user.isDeleted() ? null : user.getEmail());
        r.setRole(user.getRole());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }
}
