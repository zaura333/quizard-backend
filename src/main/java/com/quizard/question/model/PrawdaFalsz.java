package com.quizard.question.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pytanie_prawda_falsz")
@DiscriminatorValue("PRAWDA_FALSZ")
@Getter @Setter
@NoArgsConstructor
public class PrawdaFalsz extends Pytanie implements Ocenialne {

    @Column(name = "poprawna_odpowiedz", nullable = false)
    private boolean poprawnaOdpowiedz;

    @Column(nullable = false)
    private int punkty;

    @Override
    public boolean sprawdzOdpowiedz(String odpowiedz) {
        return Boolean.parseBoolean(odpowiedz.trim()) == poprawnaOdpowiedz;
    }

    @Override
    public int obliczPunkty() {
        return punkty;
    }
}
