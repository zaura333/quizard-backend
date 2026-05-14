package com.quizard.auth.service;

import com.quizard.auth.dto.LoginRequest;
import com.quizard.auth.dto.RegisterRequest;
import com.quizard.auth.jwt.JwtTokenProvider;
import com.quizard.common.exception.ConflictException;
import com.quizard.common.exception.ResourceNotFoundException;
import com.quizard.user.model.Role;
import com.quizard.user.model.User;
import com.quizard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email jest już zajęty");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Nazwa użytkownika jest już zajęta");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Nieprawidłowy email lub hasło"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Nieprawidłowy email lub hasło");
        }

        if (user.isDeleted()) {
            throw new BadCredentialsException("Konto zostało usunięte");
        }

        return jwtTokenProvider.generateToken(user.getId());
    }
}
