package com.quizard.quiz.service;

import com.quizard.common.exception.ForbiddenException;
import com.quizard.common.exception.ResourceNotFoundException;
import com.quizard.quiz.dto.CreateQuizRequest;
import com.quizard.quiz.dto.QuizSummaryResponse;
import com.quizard.quiz.model.*;
import com.quizard.quiz.repository.QuizRepository;
import com.quizard.user.model.Role;
import com.quizard.user.model.User;
import com.quizard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<QuizSummaryResponse> getPublished(Category category, Long authorId, Pageable pageable) {
        return quizRepository.findWithFilters(QuizStatus.PUBLISHED, category, authorId, null, pageable)
                .map(QuizSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public QuizSummaryResponse getByIdAsResponse(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz nie istnieje"));
        return QuizSummaryResponse.from(quiz);
    }

    public Quiz getById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz nie istnieje"));
    }

    @Transactional
    public Quiz create(CreateQuizRequest request, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));

        Quiz quiz = buildQuiz(request);
        quiz.setAuthor(author);
        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz update(Long quizId, CreateQuizRequest request, Long userId) {
        Quiz quiz = getById(quizId);
        checkEditPermission(quiz, userId);

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setCategory(request.getCategory());

        if (quiz instanceof OgraniczonymCzasem && request.getLimitCzasuSekundy() != null) {
            switch (quiz) {
                case TestWiedzy tw -> tw.setLimitCzasuSekundy(request.getLimitCzasuSekundy());
                case QuizDopasowania qd -> qd.setLimitCzasuSekundy(request.getLimitCzasuSekundy());
                case UzupelnianieLukQuiz ul -> ul.setLimitCzasuSekundy(request.getLimitCzasuSekundy());
                default -> {}
            }
        }

        return quizRepository.save(quiz);
    }

    @Transactional
    public void publish(Long quizId, Long userId) {
        Quiz quiz = getById(quizId);
        checkEditPermission(quiz, userId);
        quiz.setStatus(QuizStatus.PUBLISHED);
        quizRepository.save(quiz);
    }

    @Transactional
    public void delete(Long quizId, Long userId) {
        Quiz quiz = getById(quizId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOwner = quiz.getAuthor() != null && quiz.getAuthor().getId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("Brak uprawnień do usunięcia quizu");
        }

        quizRepository.delete(quiz);
    }

    @Transactional
    public boolean toggleLike(Long quizId, Long userId) {
        Quiz quiz = getById(quizId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));

        boolean liked = quiz.getLikedBy().contains(user);
        if (liked) {
            quiz.getLikedBy().remove(user);
        } else {
            quiz.getLikedBy().add(user);
        }
        quizRepository.save(quiz);
        return !liked;
    }

    private Quiz buildQuiz(CreateQuizRequest request) {
        return switch (request.getQuizType()) {
            case "TEST_WIEDZY" -> {
                TestWiedzy q = new TestWiedzy();
                q.setLimitCzasuSekundy(request.getLimitCzasuSekundy());
                yield q;
            }
            case "OSOBOWOSCI" -> {
                QuizOsobowosci q = new QuizOsobowosci();
                if (request.getMozliweWyniki() != null) q.setMozliweWyniki(request.getMozliweWyniki());
                yield q;
            }
            case "DOPASOWANIA" -> {
                QuizDopasowania q = new QuizDopasowania();
                q.setLimitCzasuSekundy(request.getLimitCzasuSekundy());
                yield q;
            }
            case "UZUPELNIANIE_LUK" -> {
                UzupelnianieLukQuiz q = new UzupelnianieLukQuiz();
                q.setLimitCzasuSekundy(request.getLimitCzasuSekundy());
                yield q;
            }
            case "RANKING" -> {
                Ranking q = new Ranking();
                if (request.getPoziomy() != null && !request.getPoziomy().isEmpty()) {
                    q.setPoziomy(request.getPoziomy());
                }
                yield q;
            }
            default -> throw new IllegalArgumentException("Nieznany typ quizu: " + request.getQuizType());
        };
    }

    private void checkEditPermission(Quiz quiz, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));
        boolean isOwner = quiz.getAuthor() != null && quiz.getAuthor().getId().equals(userId);
        if (user.getRole() != Role.ADMIN && !isOwner) {
            throw new ForbiddenException("Brak uprawnień do edycji quizu");
        }
    }
}
