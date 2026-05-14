package com.quizard.question.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "pytanie_multiwybor")
@DiscriminatorValue("MULTI_WYBOR")
@Getter @Setter
@NoArgsConstructor
public class MultiWybor extends Pytanie implements Ocenialne {

    @ElementCollection
    @CollectionTable(name = "multiwybor_opcje", joinColumns = @JoinColumn(name = "pytanie_id"))
    @OrderColumn(name = "opcja_order")
    @Column(name = "opcja", nullable = false)
    private List<String> opcje = new ArrayList<>();

    // przechowywane jako CSV: "opcja1,opcja2"
    @Column(name = "poprawne_odpowiedzi", nullable = false, columnDefinition = "TEXT")
    private String poprawneOdpowiedzi;

    @Column(nullable = false)
    private int punkty;

    public List<String> getPoprawneOdpowiedziAsList() {
        return Arrays.asList(poprawneOdpowiedzi.split(","));
    }

    @Override
    public boolean sprawdzOdpowiedz(String odpowiedz) {
        // odpowiedź jako CSV posortowany
        List<String> wybrane = Arrays.stream(odpowiedz.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .sorted()
                .toList();
        List<String> poprawne = getPoprawneOdpowiedziAsList().stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .sorted()
                .toList();
        return wybrane.equals(poprawne);
    }

    @Override
    public int obliczPunkty() {
        return punkty;
    }
}
