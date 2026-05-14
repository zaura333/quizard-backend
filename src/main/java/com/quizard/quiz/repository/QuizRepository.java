package com.quizard.quiz.repository;

import com.quizard.quiz.model.Category;
import com.quizard.quiz.model.Quiz;
import com.quizard.quiz.model.QuizStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Page<Quiz> findByStatus(QuizStatus status, Pageable pageable);

    Page<Quiz> findByStatusAndCategory(QuizStatus status, Category category, Pageable pageable);

    Page<Quiz> findByStatusAndAuthorId(QuizStatus status, Long authorId, Pageable pageable);

    @Query("SELECT q FROM Quiz q WHERE q.status = :status " +
           "AND (:category IS NULL OR q.category = :category) " +
           "AND (:authorId IS NULL OR q.author.id = :authorId) " +
           "AND (:type IS NULL OR TYPE(q) = :type)")
    Page<Quiz> findWithFilters(@Param("status") QuizStatus status,
                               @Param("category") Category category,
                               @Param("authorId") Long authorId,
                               @Param("type") Class<?> type,
                               Pageable pageable);
}
