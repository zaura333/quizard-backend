package com.quizard.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Wspólne DTO do tworzenia i edycji pytań.
 * Pola specyficzne dla podtypów są opcjonalne — walidacja logiczna w serwisie.
 *
 * Konwencje pól:
 *   poprawnaOdpowiedz  (Object)  – Standard: String; PrawdaFalsz: Boolean z JSON
 *   poprawneOdpowiedzi (String)  – MultiWybor: CSV, np. "opcja1,opcja2"
 *   listaPoprawnych (List)       – UzupelnianieLuk: słowa per luka
 */
@Data
public class CreatePytanieRequest {

    @NotBlank
    private String pytanieType;  // STANDARD | MULTI_WYBOR | PRAWDA_FALSZ | OSOBOWOSCI | DOPASOWANIE | UZUPELNIANIE_LUK | ELEMENT_RANKINGU

    @NotBlank
    private String tresc;

    @NotNull
    private Integer kolejnosc;

    private String podpowiedz;

    // ── Standard, MultiWybor ──────────────────────────────────────────────────
    private List<String> opcje;
    private Integer punkty;

    /**
     * Standard: wartość String (np. "Paryż")
     * PrawdaFalsz: wartość Boolean (true/false z JSON)
     * Jackson deserializuje JSON `true` → Boolean, JSON `"Paryż"` → String.
     */
    private Object poprawnaOdpowiedz;

    // ── MultiWybor ────────────────────────────────────────────────────────────
    private String poprawneOdpowiedzi;  // CSV

    // ── Osobowosci ────────────────────────────────────────────────────────────
    private Map<String, String> opcjeDoWynikow;

    // ── Dopasowanie ───────────────────────────────────────────────────────────
    private List<String> lewaKolumna;
    private List<String> prawaKolumna;
    private Map<String, String> poprawneParry;

    // ── UzupelnianieLuk ───────────────────────────────────────────────────────
    private List<String> listaPoprawnych;  // jedna odpowiedź per luka
}
