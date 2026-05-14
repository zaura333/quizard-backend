package com.quizard.comment.service;

import com.quizard.comment.dto.KomentarzResponse;
import com.quizard.comment.model.Komentarz;
import com.quizard.comment.repository.KomentarzRepository;
import com.quizard.common.exception.ForbiddenException;
import com.quizard.common.exception.ResourceNotFoundException;
import com.quizard.quiz.model.Quiz;
import com.quizard.quiz.repository.QuizRepository;
import com.quizard.user.model.Role;
import com.quizard.user.model.User;
import com.quizard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KomentarzService {

    private final KomentarzRepository komentarzRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<KomentarzResponse> getByQuiz(Long quizId, Pageable pageable) {
        return komentarzRepository.findByQuizIdOrderByCreatedAtDesc(quizId, pageable)
                .map(KomentarzResponse::from);
    }

    @Transactional
    public Komentarz add(Long quizId, String tresc, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz nie istnieje"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));

        Komentarz k = Komentarz.builder()
                .tresc(tresc)
                .quiz(quiz)
                .author(author)
                .build();

        return komentarzRepository.save(k);
    }

    @Transactional
    public void delete(Long komentarzId, Long userId) {
        Komentarz k = komentarzRepository.findById(komentarzId)
                .orElseThrow(() -> new ResourceNotFoundException("Komentarz nie istnieje"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));

        boolean isAuthor = k.getAuthor() != null && k.getAuthor().getId().equals(userId);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException("Brak uprawnień do usunięcia komentarza");
        }

        komentarzRepository.delete(k);
    }
}
