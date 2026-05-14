package com.quizard.quiz.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_dopasowania")
@DiscriminatorValue("DOPASOWANIA")
@Getter @Setter
@NoArgsConstructor
public class QuizDopasowania extends Quiz implements OgraniczonymCzasem {

    @Column(name = "limit_czasu_sekundy")
    private Integer limitCzasuSekundy;

    @Override
    public Integer getLimitCzasuSekundy() {
        return limitCzasuSekundy;
    }
}
