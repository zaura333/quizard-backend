package com.quizard.attempt.service;

import com.quizard.attempt.dto.AttemptRequest;
import com.quizard.attempt.dto.AttemptResult;
import com.quizard.common.exception.ResourceNotFoundException;
import com.quizard.question.model.*;
import com.quizard.question.repository.PytanieRepository;
import com.quizard.quiz.model.*;
import com.quizard.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final QuizRepository quizRepository;
    private final PytanieRepository pytanieRepository;

    @Transactional(readOnly = true)
    public AttemptResult evaluate(Long quizId, AttemptRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz nie istnieje"));

        validateTimeLimit(quiz, request.getStartedAtEpochMs());

        if (quiz instanceof QuizOsobowosci qo) {
            return evaluatePersonality(qo, request);
        }

        if (quiz instanceof Ranking) {
            // Ranking nie ma oceniania — zwracamy wyniki bez punktów
            return AttemptResult.builder()
                    .quizId(quizId)
                    .quizType("RANKING")
                    .build();
        }

        return evaluateScored(quiz, request);
    }

    private AttemptResult evaluateScored(Quiz quiz, AttemptRequest request) {
        Map<Long, String> answersMap = new HashMap<>();
        for (AttemptRequest.AnswerDto a : request.getAnswers()) {
            answersMap.put(a.getQuestionId(), a.getAnswer());
        }

        List<AttemptResult.QuestionResult> results = new ArrayList<>();
        int totalPoints = 0;
        int maxPoints = 0;

        List<Pytanie> pytania = pytanieRepository.findByQuizIdOrderByKolejnoscAsc(quiz.getId());
        for (Pytanie pytanie : pytania) {
            if (!(pytanie instanceof Ocenialne ocenialne)) continue;

            String given = answersMap.getOrDefault(pytanie.getId(), "");
            boolean correct = ocenialne.sprawdzOdpowiedz(given);
            int earned = correct ? ocenialne.obliczPunkty() : 0;
            totalPoints += earned;
            maxPoints += ocenialne.obliczPunkty();

            results.add(AttemptResult.QuestionResult.builder()
                    .questionId(pytanie.getId())
                    .pytanieType(pytanie.getClass().getSimpleName())
                    .correct(correct)
                    .pointsEarned(earned)
                    .maxPoints(ocenialne.obliczPunkty())
                    .givenAnswer(given)
                    .correctAnswer(extractCorrectAnswer(pytanie))
                    .build());
        }

        double percent = maxPoints > 0 ? (double) totalPoints / maxPoints * 100 : 0;

        return AttemptResult.builder()
                .quizId(quiz.getId())
                .quizType(quiz.getClass().getSimpleName())
                .totalPoints(totalPoints)
                .maxPoints(maxPoints)
                .percentScore(Math.round(percent * 10.0) / 10.0)
                .questionResults(results)
                .build();
    }

    private AttemptResult evaluatePersonality(QuizOsobowosci quiz, AttemptRequest request) {
        Map<String, Integer> votes = new HashMap<>();
        for (String wynik : quiz.getMozliweWyniki()) {
            votes.put(wynik, 0);
        }

        List<Pytanie> pytania = pytanieRepository.findByQuizIdOrderByKolejnoscAsc(quiz.getId());
        for (AttemptRequest.AnswerDto a : request.getAnswers()) {
            pytania.stream()
                    .filter(p -> p.getId().equals(a.getQuestionId()))
                    .filter(p -> p instanceof PytanieOsobowosci)
                    .map(p -> (PytanieOsobowosci) p)
                    .findFirst()
                    .ifPresent(p -> {
                        String wynik = p.getWynikDlaOpcji(a.getAnswer());
                        if (wynik != null) votes.merge(wynik, 1, Integer::sum);
                    });
        }

        String winner = votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Brak wyniku");

        return AttemptResult.builder()
                .quizId(quiz.getId())
                .quizType(quiz.getClass().getSimpleName())
                .personalityResult(winner)
                .personalityVotes(votes)
                .build();
    }

    private String extractCorrectAnswer(Pytanie pytanie) {
        return switch (pytanie) {
            case Standard s         -> s.getPoprawnaOdpowiedz();
            case PrawdaFalsz pf     -> String.valueOf(pf.isPoprawnaOdpowiedz());
            case UzupelnianieLukPytanie ul -> String.join(", ", ul.getPoprawneOdpowiedzi());
            case Dopasowanie d      -> d.getPoprawneParry().entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .reduce((a, b) -> a + " | " + b)
                    .orElse("");
            case MultiWybor m       -> m.getPoprawneOdpowiedzi();
            default                 -> null;
        };
    }

    private void validateTimeLimit(Quiz quiz, Long startedAtEpochMs) {
        if (!(quiz instanceof OgraniczonymCzasem oc)) return;
        if (oc.getLimitCzasuSekundy() == null || startedAtEpochMs == null) return;

        long elapsed = (System.currentTimeMillis() - startedAtEpochMs) / 1000;
        if (elapsed > oc.getLimitCzasuSekundy() + 5) { // +5s tolerancja sieciowa
            throw new IllegalStateException("Przekroczono limit czasu quizu");
        }
    }
}
