-- =============================================
-- V1 - Inicjalny schemat bazy danych Quizard
-- =============================================

-- Użytkownicy
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Baza quizów (JOINED inheritance)
CREATE TABLE quizzes (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_type   VARCHAR(31)  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    category    VARCHAR(50)  NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    author_id   BIGINT,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

-- Podtypy quizów
CREATE TABLE test_wiedzy (
    id                   BIGINT PRIMARY KEY,
    limit_czasu_sekundy  INT,
    CONSTRAINT fk_tw FOREIGN KEY (id) REFERENCES quizzes (id) ON DELETE CASCADE
);

CREATE TABLE quiz_osobowosci (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_qo FOREIGN KEY (id) REFERENCES quizzes (id) ON DELETE CASCADE
);

CREATE TABLE quiz_osobowosci_wyniki (
    quiz_id  BIGINT       NOT NULL,
    wynik    VARCHAR(255) NOT NULL,
    CONSTRAINT fk_qow FOREIGN KEY (quiz_id) REFERENCES quiz_osobowosci (id) ON DELETE CASCADE
);

CREATE TABLE quiz_dopasowania (
    id                   BIGINT PRIMARY KEY,
    limit_czasu_sekundy  INT,
    CONSTRAINT fk_qd FOREIGN KEY (id) REFERENCES quizzes (id) ON DELETE CASCADE
);

CREATE TABLE uzupelnianie_luk_quiz (
    id                   BIGINT PRIMARY KEY,
    limit_czasu_sekundy  INT,
    CONSTRAINT fk_ulq FOREIGN KEY (id) REFERENCES quizzes (id) ON DELETE CASCADE
);

CREATE TABLE ranking (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_r FOREIGN KEY (id) REFERENCES quizzes (id) ON DELETE CASCADE
);

CREATE TABLE ranking_poziomy (
    quiz_id       BIGINT       NOT NULL,
    poziom        VARCHAR(50)  NOT NULL,
    poziom_order  INT          NOT NULL,
    CONSTRAINT fk_rp FOREIGN KEY (quiz_id) REFERENCES ranking (id) ON DELETE CASCADE
);

-- Polubienia
CREATE TABLE user_quiz_likes (
    user_id  BIGINT NOT NULL,
    quiz_id  BIGINT NOT NULL,
    PRIMARY KEY (user_id, quiz_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_like_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE
);

-- Baza pytań (JOINED inheritance)
CREATE TABLE pytania (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    pytanie_type VARCHAR(31)  NOT NULL,
    tresc        TEXT         NOT NULL,
    quiz_id      BIGINT       NOT NULL,
    kolejnosc    INT          NOT NULL DEFAULT 0,
    podpowiedz   TEXT,
    CONSTRAINT fk_pytanie_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE
);

-- Podtypy pytań
CREATE TABLE pytanie_standard (
    id                   BIGINT       PRIMARY KEY,
    poprawna_odpowiedz   VARCHAR(500) NOT NULL,
    punkty               INT          NOT NULL,
    CONSTRAINT fk_ps FOREIGN KEY (id) REFERENCES pytania (id) ON DELETE CASCADE
);

CREATE TABLE standard_opcje (
    pytanie_id   BIGINT       NOT NULL,
    opcja        VARCHAR(500) NOT NULL,
    opcja_order  INT          NOT NULL,
    CONSTRAINT fk_so FOREIGN KEY (pytanie_id) REFERENCES pytanie_standard (id) ON DELETE CASCADE
);

CREATE TABLE pytanie_multiwybor (
    id                    BIGINT PRIMARY KEY,
    poprawne_odpowiedzi   TEXT   NOT NULL,
    punkty                INT    NOT NULL,
    CONSTRAINT fk_pm FOREIGN KEY (id) REFERENCES pytania (id) ON DELETE CASCADE
);

CREATE TABLE multiwybor_opcje (
    pytanie_id   BIGINT       NOT NULL,
    opcja        VARCHAR(500) NOT NULL,
    opcja_order  INT          NOT NULL,
    CONSTRAINT fk_mo FOREIGN KEY (pytanie_id) REFERENCES pytanie_multiwybor (id) ON DELETE CASCADE
);

CREATE TABLE pytanie_prawda_falsz (
    id                   BIGINT  PRIMARY KEY,
    poprawna_odpowiedz   BOOLEAN NOT NULL,
    punkty               INT     NOT NULL,
    CONSTRAINT fk_ppf FOREIGN KEY (id) REFERENCES pytania (id) ON DELETE CASCADE
);

CREATE TABLE pytanie_osobowosci (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_po FOREIGN KEY (id) REFERENCES pytania (id) ON DELETE CASCADE
);

CREATE TABLE osobowosci_opcje (
    pytanie_id  BIGINT       NOT NULL,
    opcja       VARCHAR(500) NOT NULL,
    wynik       VARCHAR(255) NOT NULL,
    CONSTRAINT fk_oo FOREIGN KEY (pytanie_id) REFERENCES pytanie_osobowosci (id) ON DELETE CASCADE
);

CREATE TABLE pytanie_dopasowanie (
    id      BIGINT PRIMARY KEY,
    punkty  INT    NOT NULL,
    CONSTRAINT fk_pd FOREIGN KEY (id) REFERENCES pytania (id) ON DELETE CASCADE
);

CREATE TABLE dopasowanie_lewa (
    pytanie_id     BIGINT       NOT NULL,
    element        VARCHAR(500) NOT NULL,
    element_order  INT          NOT NULL,
    CONSTRAINT fk_dl FOREIGN KEY (pytanie_id) REFERENCES pytanie_dopasowanie (id) ON DELETE CASCADE
);

CREATE TABLE dopasowanie_prawa (
    pytanie_id     BIGINT       NOT NULL,
    element        VARCHAR(500) NOT NULL,
    element_order  INT          NOT NULL,
    CONSTRAINT fk_dp FOREIGN KEY (pytanie_id) REFERENCES pytanie_dopasowanie (id) ON DELETE CASCADE
);

CREATE TABLE dopasowanie_pary (
    pytanie_id  BIGINT       NOT NULL,
    lewy        VARCHAR(500) NOT NULL,
    prawy       VARCHAR(500) NOT NULL,
    CONSTRAINT fk_dpp FOREIGN KEY (pytanie_id) REFERENCES pytanie_dopasowanie (id) ON DELETE CASCADE
);

CREATE TABLE pytanie_uzupelnianie_luk (
    id      BIGINT PRIMARY KEY,
    punkty  INT    NOT NULL,
    CONSTRAINT fk_pul FOREIGN KEY (id) REFERENCES pytania (id) ON DELETE CASCADE
);

CREATE TABLE uzupelnianie_odpowiedzi (
    pytanie_id       BIGINT       NOT NULL,
    odpowiedz        VARCHAR(500) NOT NULL,
    odpowiedz_order  INT          NOT NULL,
    CONSTRAINT fk_uo FOREIGN KEY (pytanie_id) REFERENCES pytanie_uzupelnianie_luk (id) ON DELETE CASCADE
);

CREATE TABLE element_rankingu (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_er FOREIGN KEY (id) REFERENCES pytania (id) ON DELETE CASCADE
);

-- Komentarze
CREATE TABLE komentarze (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tresc       TEXT     NOT NULL,
    quiz_id     BIGINT   NOT NULL,
    author_id   BIGINT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_kom_quiz   FOREIGN KEY (quiz_id)   REFERENCES quizzes (id) ON DELETE CASCADE,
    CONSTRAINT fk_kom_author FOREIGN KEY (author_id) REFERENCES users   (id) ON DELETE SET NULL
);
