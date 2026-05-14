package com.quizard.quiz.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "ranking")
@DiscriminatorValue("RANKING")
@Getter @Setter
@NoArgsConstructor
public class Ranking extends Quiz {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ranking_poziomy", joinColumns = @JoinColumn(name = "quiz_id"))
    @OrderColumn(name = "poziom_order")
    @Column(name = "poziom", nullable = false)
    private List<String> poziomy = List.of("S", "A", "B", "C", "D");
}
