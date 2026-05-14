package com.quizard.question.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "pytanie_osobowosci")
@DiscriminatorValue("OSOBOWOSCI")
@Getter @Setter
@NoArgsConstructor
public class PytanieOsobowosci extends Pytanie implements Ocenialne {

    // klucz: treść opcji, wartość: wynik osobowości na który zbiera głos
    @ElementCollection
    @CollectionTable(name = "osobowosci_opcje", joinColumns = @JoinColumn(name = "pytanie_id"))
    @MapKeyColumn(name = "opcja")
    @Column(name = "wynik")
    private Map<String, String> opcjeDoWynikow = new LinkedHashMap<>();

    @Override
    public boolean sprawdzOdpowiedz(String odpowiedz) {
        return opcjeDoWynikow.containsKey(odpowiedz);
    }

    @Override
    public int obliczPunkty() {
        return 0; // brak punktacji — głosowanie na wynik
    }

    public String getWynikDlaOpcji(String opcja) {
        return opcjeDoWynikow.get(opcja);
    }
}
