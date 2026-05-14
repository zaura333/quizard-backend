package com.quizard.quiz.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_wiedzy")
@DiscriminatorValue("TEST_WIEDZY")
@Getter @Setter
@NoArgsConstructor
public class TestWiedzy extends Quiz implements OgraniczonymCzasem {

    @Column(name = "limit_czasu_sekundy")
    private Integer limitCzasuSekundy;

    @Override
    public Integer getLimitCzasuSekundy() {
        return limitCzasuSekundy;
    }
}
