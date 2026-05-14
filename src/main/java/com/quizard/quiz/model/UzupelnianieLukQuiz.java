package com.quizard.quiz.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "uzupelnianie_luk_quiz")
@DiscriminatorValue("UZUPELNIANIE_LUK")
@Getter @Setter
@NoArgsConstructor
public class UzupelnianieLukQuiz extends Quiz implements OgraniczonymCzasem {

    @Column(name = "limit_czasu_sekundy")
    private Integer limitCzasuSekundy;

    @Override
    public Integer getLimitCzasuSekundy() {
        return limitCzasuSekundy;
    }
}
