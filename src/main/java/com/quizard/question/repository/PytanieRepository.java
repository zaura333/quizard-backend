package com.quizard.question.repository;

import com.quizard.question.model.Pytanie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PytanieRepository extends JpaRepository<Pytanie, Long> {
    List<Pytanie> findByQuizIdOrderByKolejnoscAsc(Long quizId);
    void deleteAllByQuizId(Long quizId);
}
