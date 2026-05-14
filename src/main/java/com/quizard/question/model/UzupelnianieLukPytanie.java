package com.quizard.question.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "pytanie_uzupelnianie_luk")
@DiscriminatorValue("UZUPELNIANIE_LUK")
@Getter @Setter
@NoArgsConstructor
public class UzupelnianieLukPytanie extends Pytanie implements Ocenialne {

    // treść z lukami oznaczonymi jako "___", np. "Stolica Polski to ___"
    @ElementCollection
    @CollectionTable(name = "uzupelnianie_odpowiedzi", joinColumns = @JoinColumn(name = "pytanie_id"))
    @OrderColumn(name = "odpowiedz_order")
    @Column(name = "odpowiedz", nullable = false)
    private List<String> poprawneOdpowiedzi = new ArrayList<>();

    @Column(nullable = false)
    private int punkty;

    // odpowiedź jako CSV — po jednej wartości na lukę, w kolejności
    @Override
    public boolean sprawdzOdpowiedz(String odpowiedz) {
        List<String> podane = Arrays.stream(odpowiedz.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        List<String> poprawne = poprawneOdpowiedzi.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        return podane.equals(poprawne);
    }

    @Override
    public int obliczPunkty() {
        return punkty;
    }
}
