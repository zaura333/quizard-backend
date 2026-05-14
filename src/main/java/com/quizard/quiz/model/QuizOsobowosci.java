package com.quizard.quiz.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_osobowosci")
@DiscriminatorValue("OSOBOWOSCI")
@Getter @Setter
@NoArgsConstructor
public class QuizOsobowosci extends Quiz {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quiz_osobowosci_wyniki", joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "wynik", nullable = false)
    private List<String> mozliweWyniki = new ArrayList<>();
}
