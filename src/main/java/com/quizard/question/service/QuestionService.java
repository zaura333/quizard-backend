package com.quizard.question.service;

import com.quizard.common.exception.ForbiddenException;
import com.quizard.common.exception.ResourceNotFoundException;
import com.quizard.question.dto.CreatePytanieRequest;
import com.quizard.question.dto.PytanieResponse;
import com.quizard.question.model.*;
import com.quizard.question.repository.PytanieRepository;
import com.quizard.quiz.model.Quiz;
import com.quizard.quiz.repository.QuizRepository;
import com.quizard.user.model.Role;
import com.quizard.user.model.User;
import com.quizard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final PytanieRepository pytanieRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    // ─── Pobieranie ───────────────────────────────────────────────────────────

    /**
     * Zwraca pytania quizu posortowane po kolejności.
     * DTO są budowane WEWNĄTRZ transakcji — lazy loading bezpieczny.
     */
    @Transactional(readOnly = true)
    public List<PytanieResponse> getByQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz nie istnieje");
        }
        return pytanieRepository.findByQuizIdOrderByKolejnoscAsc(quizId)
                .stream()
                .map(PytanieResponse::from)   // sesja wciąż otwarta — lazy OK
                .toList();
    }

    // ─── Tworzenie ────────────────────────────────────────────────────────────

    @Transactional
    public PytanieResponse add(Long quizId, CreatePytanieRequest req, Long userId) {
        Quiz quiz = getQuizOrThrow(quizId);
        checkEditPermission(quiz, userId);

        Pytanie pytanie = buildPytanie(req, quiz);
        Pytanie saved = pytanieRepository.save(pytanie);
        return PytanieResponse.from(saved);  // w transakcji — lazy OK
    }

    // ─── Edycja ───────────────────────────────────────────────────────────────

    @Transactional
    public PytanieResponse update(Long quizId, Long pytanieId, CreatePytanieRequest req, Long userId) {
        Quiz quiz = getQuizOrThrow(quizId);
        checkEditPermission(quiz, userId);

        Pytanie existing = pytanieRepository.findById(pytanieId)
                .orElseThrow(() -> new ResourceNotFoundException("Pytanie nie istnieje"));

        if (!existing.getQuiz().getId().equals(quizId)) {
            throw new ForbiddenException("Pytanie nie należy do tego quizu");
        }

        // Przy hierarchii JOINED + GenerationType.IDENTITY nie możemy wymusić tego samego ID.
        // Usuwamy starą encję, wstawiamy nową — frontend musi odświeżyć listę pytań.
        pytanieRepository.delete(existing);
        pytanieRepository.flush();

        Pytanie newPytanie = buildPytanie(req, quiz);
        Pytanie saved = pytanieRepository.save(newPytanie);
        return PytanieResponse.from(saved);
    }

    // ─── Usuwanie ─────────────────────────────────────────────────────────────

    @Transactional
    public void delete(Long quizId, Long pytanieId, Long userId) {
        Quiz quiz = getQuizOrThrow(quizId);
        checkEditPermission(quiz, userId);

        Pytanie pytanie = pytanieRepository.findById(pytanieId)
                .orElseThrow(() -> new ResourceNotFoundException("Pytanie nie istnieje"));

        if (!pytanie.getQuiz().getId().equals(quizId)) {
            throw new ForbiddenException("Pytanie nie należy do tego quizu");
        }

        pytanieRepository.delete(pytanie);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Quiz getQuizOrThrow(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz nie istnieje"));
    }

    private void checkEditPermission(Quiz quiz, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));
        boolean isOwner = quiz.getAuthor() != null && quiz.getAuthor().getId().equals(userId);
        if (user.getRole() != Role.ADMIN && !isOwner) {
            throw new ForbiddenException("Brak uprawnień do edycji pytań tego quizu");
        }
    }

    /**
     * Buduje encję Pytanie z DTO.
     * Lazy loading tu nie jest problemem — tworzymy nowe obiekty.
     */
    private Pytanie buildPytanie(CreatePytanieRequest req, Quiz quiz) {
        Pytanie pytanie = switch (req.getPytanieType()) {
            case "STANDARD" -> buildStandard(req);
            case "MULTI_WYBOR" -> buildMultiWybor(req);
            case "PRAWDA_FALSZ" -> buildPrawdaFalsz(req);
            case "OSOBOWOSCI" -> buildOsobowosci(req);
            case "DOPASOWANIE" -> buildDopasowanie(req);
            case "UZUPELNIANIE_LUK" -> buildUzupelnianieLuk(req);
            case "ELEMENT_RANKINGU" -> buildElementRankingu();
            default -> throw new IllegalArgumentException("Nieznany typ pytania: " + req.getPytanieType());
        };

        pytanie.setTresc(req.getTresc());
        pytanie.setKolejnosc(req.getKolejnosc());
        pytanie.setPodpowiedz(req.getPodpowiedz());
        pytanie.setQuiz(quiz);
        return pytanie;
    }

    private Standard buildStandard(CreatePytanieRequest req) {
        Standard s = new Standard();
        if (req.getOpcje() != null) s.getOpcje().addAll(req.getOpcje());
        s.setPoprawnaOdpowiedz(asString(req.getPoprawnaOdpowiedz()));
        s.setPunkty(req.getPunkty() != null ? req.getPunkty() : 1);
        return s;
    }

    private MultiWybor buildMultiWybor(CreatePytanieRequest req) {
        MultiWybor m = new MultiWybor();
        if (req.getOpcje() != null) m.getOpcje().addAll(req.getOpcje());
        m.setPoprawneOdpowiedzi(req.getPoprawneOdpowiedzi() != null ? req.getPoprawneOdpowiedzi() : "");
        m.setPunkty(req.getPunkty() != null ? req.getPunkty() : 1);
        return m;
    }

    private PrawdaFalsz buildPrawdaFalsz(CreatePytanieRequest req) {
        PrawdaFalsz pf = new PrawdaFalsz();
        pf.setPoprawnaOdpowiedz(asBool(req.getPoprawnaOdpowiedz()));
        pf.setPunkty(req.getPunkty() != null ? req.getPunkty() : 1);
        return pf;
    }

    private PytanieOsobowosci buildOsobowosci(CreatePytanieRequest req) {
        PytanieOsobowosci po = new PytanieOsobowosci();
        if (req.getOpcjeDoWynikow() != null) {
            po.getOpcjeDoWynikow().putAll(req.getOpcjeDoWynikow());
        }
        return po;
    }

    private Dopasowanie buildDopasowanie(CreatePytanieRequest req) {
        Dopasowanie d = new Dopasowanie();
        if (req.getLewaKolumna() != null) d.getLewaKolumna().addAll(req.getLewaKolumna());
        if (req.getPrawaKolumna() != null) d.getPrawaKolumna().addAll(req.getPrawaKolumna());
        if (req.getPoprawneParry() != null) d.getPoprawneParry().putAll(req.getPoprawneParry());
        d.setPunkty(req.getPunkty() != null ? req.getPunkty() : 1);
        return d;
    }

    private UzupelnianieLukPytanie buildUzupelnianieLuk(CreatePytanieRequest req) {
        UzupelnianieLukPytanie ul = new UzupelnianieLukPytanie();
        if (req.getListaPoprawnych() != null) {
            ul.getPoprawneOdpowiedzi().addAll(req.getListaPoprawnych());
        }
        ul.setPunkty(req.getPunkty() != null ? req.getPunkty() : 1);
        return ul;
    }

    private ElementRankingu buildElementRankingu() {
        return new ElementRankingu();
    }

    /**
     * Konwertuje Object (String lub Boolean z JSON) na String.
     */
    private String asString(Object value) {
        if (value == null) return "";
        return value.toString();
    }

    /**
     * Konwertuje Object (Boolean lub String z JSON) na boolean.
     * JSON `true` → Boolean; JSON `"true"` → String.
     */
    private boolean asBool(Object value) {
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return Boolean.parseBoolean(s);
        return false;
    }
}
