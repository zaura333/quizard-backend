package com.quizard.question.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "pytanie_dopasowanie")
@DiscriminatorValue("DOPASOWANIE")
@Getter @Setter
@NoArgsConstructor
public class Dopasowanie extends Pytanie implements Ocenialne {

    @ElementCollection
    @CollectionTable(name = "dopasowanie_lewa", joinColumns = @JoinColumn(name = "pytanie_id"))
    @OrderColumn(name = "element_order")
    @Column(name = "element", nullable = false)
    private List<String> lewaKolumna = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "dopasowanie_prawa", joinColumns = @JoinColumn(name = "pytanie_id"))
    @OrderColumn(name = "element_order")
    @Column(name = "element", nullable = false)
    private List<String> prawaKolumna = new ArrayList<>();

    // klucz: element lewej kolumny, wartość: poprawny element prawej
    @ElementCollection
    @CollectionTable(name = "dopasowanie_pary", joinColumns = @JoinColumn(name = "pytanie_id"))
    @MapKeyColumn(name = "lewy")
    @Column(name = "prawy")
    private Map<String, String> poprawneParry = new LinkedHashMap<>();

    @Column(nullable = false)
    private int punkty;

    // odpowiedź jako "lewy1:prawy1,lewy2:prawy2"
    @Override
    public boolean sprawdzOdpowiedz(String odpowiedz) {
        Map<String, String> podane = parsujOdpowiedz(odpowiedz);
        return podane.equals(poprawneParry);
    }

    @Override
    public int obliczPunkty() {
        return punkty;
    }

    private Map<String, String> parsujOdpowiedz(String odpowiedz) {
        Map<String, String> wynik = new LinkedHashMap<>();
        for (String para : odpowiedz.split(",")) {
            String[] czesci = para.split(":");
            if (czesci.length == 2) {
                wynik.put(czesci[0].trim(), czesci[1].trim());
            }
        }
        return wynik;
    }
}
