package com.quizard.comment.repository;

import com.quizard.comment.model.Komentarz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KomentarzRepository extends JpaRepository<Komentarz, Long> {
    Page<Komentarz> findByQuizIdOrderByCreatedAtDesc(Long quizId, Pageable pageable);
}
