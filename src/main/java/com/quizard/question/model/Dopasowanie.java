package com.quizard.question.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // odpowiedź jako JSON: {"lewy1":"prawy1","lewy2":"prawy2"}
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
        if (odpowiedz == null || odpowiedz.isBlank()) return new LinkedHashMap<>();
        try {
            return MAPPER.readValue(odpowiedz, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }
}
