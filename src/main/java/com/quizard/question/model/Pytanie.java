package com.quizard.question.model;

import com.quizard.quiz.model.Quiz;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pytania")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "pytanie_type", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public abstract class Pytanie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tresc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private int kolejnosc;

    @Column(columnDefinition = "TEXT")
    private String podpowiedz;
}
