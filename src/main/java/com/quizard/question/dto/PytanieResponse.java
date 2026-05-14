package com.quizard.question.dto;

import com.quizard.question.model.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO odpowiedzi dla pytania.
 *
 * WAŻNE: metoda from() musi być wywoływana wewnątrz transakcji Hibernate,
 * ponieważ pola @ElementCollection są ładowane leniwie.
 * QuestionService wywołuje ją wewnątrz @Transactional — nie należy
 * wywoływać tej metody w kontrolerze po zamknięciu sesji.
 */
@Data
public class PytanieResponse {

    private Long id;
    private String pytanieType;
    private String tresc;
    private Integer kolejnosc;
    private String podpowiedz;

    // ── Standard, MultiWybor ──────────────────────────────────────────────────
    private List<String> opcje;
    private Integer punkty;

    // ── Standard ──────────────────────────────────────────────────────────────
    private String poprawnaOdpowiedz;           // String (np. "Paryż")

    // ── MultiWybor ────────────────────────────────────────────────────────────
    private String poprawneOdpowiedzi;          // CSV (np. "opcja1,opcja2")

    // ── PrawdaFalsz ───────────────────────────────────────────────────────────
    private Boolean poprawnaOdpowiedzBool;

    // ── Osobowosci ────────────────────────────────────────────────────────────
    private Map<String, String> opcjeDoWynikow;

    // ── Dopasowanie ───────────────────────────────────────────────────────────
    private List<String> lewaKolumna;
    private List<String> prawaKolumna;
    private Map<String, String> poprawneParry;

    // ── UzupelnianieLuk ───────────────────────────────────────────────────────
    private List<String> listaPoprawnych;       // lista słów per luka

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Buduje DTO z encji Pytanie.
     * Musi być wywołane wewnątrz aktywnej sesji Hibernate (@Transactional).
     */
    public static PytanieResponse from(Pytanie pytanie) {
        PytanieResponse r = new PytanieResponse();
        r.setId(pytanie.getId());
        r.setTresc(pytanie.getTresc());
        r.setKolejnosc(pytanie.getKolejnosc());
        r.setPodpowiedz(pytanie.getPodpowiedz());

        switch (pytanie) {
            case Standard s -> {
                r.setPytanieType("STANDARD");
                r.setOpcje(List.copyOf(s.getOpcje()));          // dostęp do lazy w transakcji
                r.setPoprawnaOdpowiedz(s.getPoprawnaOdpowiedz());
                r.setPunkty(s.getPunkty());
            }
            case MultiWybor m -> {
                r.setPytanieType("MULTI_WYBOR");
                r.setOpcje(List.copyOf(m.getOpcje()));          // lazy
                r.setPoprawneOdpowiedzi(m.getPoprawneOdpowiedzi());
                r.setPunkty(m.getPunkty());
            }
            case PrawdaFalsz pf -> {
                r.setPytanieType("PRAWDA_FALSZ");
                r.setPoprawnaOdpowiedzBool(pf.isPoprawnaOdpowiedz());
                r.setPunkty(pf.getPunkty());
            }
            case PytanieOsobowosci po -> {
                r.setPytanieType("OSOBOWOSCI");
                r.setOpcjeDoWynikow(Map.copyOf(po.getOpcjeDoWynikow())); // lazy
            }
            case Dopasowanie d -> {
                r.setPytanieType("DOPASOWANIE");
                r.setLewaKolumna(List.copyOf(d.getLewaKolumna()));      // lazy
                r.setPrawaKolumna(List.copyOf(d.getPrawaKolumna()));    // lazy
                r.setPoprawneParry(Map.copyOf(d.getPoprawneParry()));   // lazy
                r.setPunkty(d.getPunkty());
            }
            case UzupelnianieLukPytanie ul -> {
                r.setPytanieType("UZUPELNIANIE_LUK");
                r.setListaPoprawnych(List.copyOf(ul.getPoprawneOdpowiedzi())); // lazy
                r.setPunkty(ul.getPunkty());
            }
            case ElementRankingu ignored -> {
                r.setPytanieType("ELEMENT_RANKINGU");
            }
            default -> r.setPytanieType("UNKNOWN");
        }

        return r;
    }
}
