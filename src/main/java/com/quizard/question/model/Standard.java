package com.quizard.question.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pytanie_standard")
@DiscriminatorValue("STANDARD")
@Getter @Setter
@NoArgsConstructor
public class Standard extends Pytanie implements Ocenialne {

    @ElementCollection
    @CollectionTable(name = "standard_opcje", joinColumns = @JoinColumn(name = "pytanie_id"))
    @OrderColumn(name = "opcja_order")
    @Column(name = "opcja", nullable = false)
    private List<String> opcje = new ArrayList<>();

    @Column(name = "poprawna_odpowiedz", nullable = false)
    private String poprawnaOdpowiedz;

    @Column(nullable = false)
    private int punkty;

    @Override
    public boolean sprawdzOdpowiedz(String odpowiedz) {
        return poprawnaOdpowiedz != null && poprawnaOdpowiedz.equalsIgnoreCase(odpowiedz.trim());
    }

    @Override
    public int obliczPunkty() {
        return punkty;
    }
}
