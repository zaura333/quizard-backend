-- =============================================
-- V2 - Dane testowe (seed)
-- Hasła użytkowników:
--   admin  → Admin123!
--   janek  → Haslo123!
--   ania   → Haslo123!
--   piotr  → Haslo123!
--   kasia  → Haslo123!
-- =============================================

-- ─────────────────────────────────────────────
-- UŻYTKOWNICY (id 1-5)
-- ─────────────────────────────────────────────
INSERT INTO users (id, username, email, password, role, deleted, created_at) VALUES
(1, 'admin',  'admin@quizard.pl',  '$2b$10$kUkGoC7RT74DJw4fMWsoWeXVDyjp9PTB4.vQNIfH2LdMq.fH5WG/G', 'ADMIN', false, NOW()),
(2, 'janek',  'janek@quizard.pl',  '$2b$10$vcrX9fpviRP5Fjk/KwLvLucG.mP3fNG7iX4CHgeFL00Td61Gt1sem', 'USER',  false, NOW()),
(3, 'ania',   'ania@quizard.pl',   '$2b$10$StIwvCMTzATkEQVD9tUaEu9DXYz4Fi.3pxHaQdLhJrObcESIik1Ru', 'USER',  false, NOW()),
(4, 'piotr',  'piotr@quizard.pl',  '$2b$10$4ir1rR5VNT57w7Eo7X/QBOl.SR6DRpA3IbbzCVYV2n0TULrceQ2Xq', 'USER',  false, NOW()),
(5, 'kasia',  'kasia@quizard.pl',  '$2b$10$p1CwXPHhI/G7AR2fY8JavuvBUwlLqe0NChCJJahkYkjwEVGzJ/tS.', 'USER',  false, NOW());

-- ─────────────────────────────────────────────
-- QUIZY — tabela bazowa (id 1-10)
-- ─────────────────────────────────────────────
INSERT INTO quizzes (id, quiz_type, title, description, category, status, author_id, created_at, updated_at) VALUES
(1,  'TEST_WIEDZY',      'Stolice Europy',                     'Sprawdź czy znasz stolice europejskich krajów!',          'GEOGRAFIA',    'PUBLISHED', 2, NOW(), NOW()),
(2,  'TEST_WIEDZY',      'Historia Polski',                    'Quiz o najważniejszych wydarzeniach w historii Polski.',  'HISTORIA',     'PUBLISHED', 3, NOW(), NOW()),
(3,  'OSOBOWOSCI',       'Jaki jesteś typem gracza?',          'Odkryj swój styl grania.',                               'ROZRYWKA',     'PUBLISHED', 4, NOW(), NOW()),
(4,  'DOPASOWANIA',      'Dopasuj wynalazców do wynalazków',   'Czy wiesz kto co wynalazł?',                             'NAUKA',        'PUBLISHED', 2, NOW(), NOW()),
(5,  'UZUPELNIANIE_LUK', 'Uzupełnij cytaty filmowe',           'Klasyczne cytaty z filmów — uzupełnij brakujące słowa.', 'FILM',         'PUBLISHED', 5, NOW(), NOW()),
(6,  'RANKING',          'Planety Układu Słonecznego',         'Uszereguj planety od najmniejszej do największej.',      'NAUKA',        'PUBLISHED', 3, NOW(), NOW()),
(7,  'TEST_WIEDZY',      'Piłka nożna — podstawy',             'Sprawdź swoją wiedzę o piłce nożnej!',                   'SPORT',        'PUBLISHED', 4, NOW(), NOW()),
(8,  'OSOBOWOSCI',       'Który znak zodiaku do Ciebie pasuje?','Odkryj swój znak na podstawie osobowości.',             'ROZRYWKA',     'DRAFT',     5, NOW(), NOW()),
(9,  'TEST_WIEDZY',      'Technologia i IT',                   'Quiz dla miłośników komputerów i programowania.',        'TECHNOLOGIA',  'PUBLISHED', 2, NOW(), NOW()),
(10, 'DOPASOWANIA',      'Flagi i kraje',                      'Dopasuj kraj do opisu jego flagi.',                      'GEOGRAFIA',    'PUBLISHED', 1, NOW(), NOW());

-- ─────────────────────────────────────────────
-- PODTYPY QUIZÓW
-- ─────────────────────────────────────────────
INSERT INTO test_wiedzy (id, limit_czasu_sekundy) VALUES
(1, NULL),
(2, 300),
(7, NULL),
(9, 120);

INSERT INTO quiz_osobowosci (id) VALUES (3), (8);

INSERT INTO quiz_osobowosci_wyniki (quiz_id, wynik) VALUES
(3, 'Strateg'),
(3, 'Action Hero'),
(3, 'Eksplorator'),
(3, 'Social Gracz'),
(8, 'Baran'),
(8, 'Byk');

INSERT INTO quiz_dopasowania (id, limit_czasu_sekundy) VALUES
(4, NULL),
(10, NULL);

INSERT INTO uzupelnianie_luk_quiz (id, limit_czasu_sekundy) VALUES
(5, 180);

INSERT INTO ranking (id) VALUES (6);

INSERT INTO ranking_poziomy (quiz_id, poziom, poziom_order) VALUES
(6, 'S', 0),
(6, 'A', 1),
(6, 'B', 2),
(6, 'C', 3),
(6, 'D', 4);

-- ─────────────────────────────────────────────
-- PYTANIA — tabela bazowa
-- Quiz 1 (Stolice Europy):      id  1-4   pytanie_standard
-- Quiz 2 (Historia Polski):     id  5-9   3x prawda_falsz + 2x standard
-- Quiz 3 (Typy gracza):         id 10-13  pytanie_osobowosci
-- Quiz 4 (Wynalazcy):           id 14-15  pytanie_dopasowanie
-- Quiz 5 (Cytaty filmowe):      id 16-18  pytanie_uzupelnianie_luk
-- Quiz 6 (Planety - ranking):   id 19-23  element_rankingu
-- Quiz 7 (Piłka nożna):         id 24-26  pytanie_standard
-- Quiz 8 (Znaki zodiaku):       id 27-28  pytanie_osobowosci
-- Quiz 9 (Technologia i IT):    id 29-32  3x standard + 1x prawda_falsz
-- Quiz 10 (Flagi i kraje):      id 33-34  pytanie_dopasowanie
-- ─────────────────────────────────────────────
INSERT INTO pytania (id, pytanie_type, tresc, quiz_id, kolejnosc, podpowiedz) VALUES
-- Quiz 1
(1,  'STANDARD',          'Jaka jest stolica Francji?',                                    1,  1, NULL),
(2,  'STANDARD',          'Jaka jest stolica Niemiec?',                                    1,  2, NULL),
(3,  'STANDARD',          'Jaka jest stolica Hiszpanii?',                                  1,  3, NULL),
(4,  'STANDARD',          'Jaka jest stolica Włoch?',                                      1,  4, NULL),
-- Quiz 2
(5,  'PRAWDA_FALSZ',      'Polska odzyskała niepodległość w 1918 roku.',                   2,  1, NULL),
(6,  'PRAWDA_FALSZ',      'Jan III Sobieski wygrał bitwę pod Wiedniem w 1683 roku.',       2,  2, NULL),
(7,  'PRAWDA_FALSZ',      'Polska była członkiem NATO przed rokiem 1989.',                 2,  3, 'NATO powstało w 1949 roku.'),
(8,  'STANDARD',          'Kto był pierwszym koronowanym królem Polski?',                  2,  4, NULL),
(9,  'STANDARD',          'W którym roku wybuchło Powstanie Warszawskie?',                 2,  5, NULL),
-- Quiz 3
(10, 'OSOBOWOSCI',        'Gdy dostajesz nową grę, co robisz najpierw?',                   3,  1, NULL),
(11, 'OSOBOWOSCI',        'Co sprawia Ci największą radość w graniu?',                     3,  2, NULL),
(12, 'OSOBOWOSCI',        'Jak reagujesz na przegraną?',                                   3,  3, NULL),
(13, 'OSOBOWOSCI',        'Jakie gry preferujesz?',                                        3,  4, NULL),
-- Quiz 4
(14, 'DOPASOWANIE',       'Dopasuj wynalazcę do jego wynalazku.',                          4,  1, NULL),
(15, 'DOPASOWANIE',       'Dopasuj naukowca do odkrycia lub teorii.',                      4,  2, NULL),
-- Quiz 5
(16, 'UZUPELNIANIE_LUK',  'Frankly, my dear, I don''t give a ___.',                        5,  1, 'Film z 1939 roku osadzony w czasie Wojny Secesyjnej.'),
(17, 'UZUPELNIANIE_LUK',  'May the ___ be with you.',                                      5,  2, NULL),
(18, 'UZUPELNIANIE_LUK',  'To ___ or not to ___, that is the question.',                   5,  3, 'Hamlet, William Shakespeare.'),
-- Quiz 6
(19, 'ELEMENT_RANKINGU',  'Merkury',                                                        6,  1, NULL),
(20, 'ELEMENT_RANKINGU',  'Mars',                                                           6,  2, NULL),
(21, 'ELEMENT_RANKINGU',  'Wenus',                                                          6,  3, NULL),
(22, 'ELEMENT_RANKINGU',  'Ziemia',                                                         6,  4, NULL),
(23, 'ELEMENT_RANKINGU',  'Jowisz',                                                         6,  5, NULL),
-- Quiz 7
(24, 'STANDARD',          'Który klub zdobył najwięcej tytułów Ligi Mistrzów UEFA?',       7,  1, NULL),
(25, 'STANDARD',          'Ile zawodników jednej drużyny gra jednocześnie na boisku?',     7,  2, NULL),
(26, 'STANDARD',          'Jak nazywa się najważniejszy puchar krajowy w Polsce?',         7,  3, NULL),
-- Quiz 8
(27, 'OSOBOWOSCI',        'Jak reagujesz na stres?',                                       8,  1, NULL),
(28, 'OSOBOWOSCI',        'Jaki weekend preferujesz?',                                     8,  2, NULL),
-- Quiz 9
(29, 'STANDARD',          'Co oznacza skrót HTTP?',                                        9,  1, NULL),
(30, 'STANDARD',          'Który język programowania jest najstarszy?',                    9,  2, 'Wybierz spośród podanych opcji.'),
(31, 'STANDARD',          'Co to jest RAM?',                                               9,  3, NULL),
(32, 'PRAWDA_FALSZ',      'Python jest językiem kompilowanym (nie interpretowanym).',       9,  4, NULL),
-- Quiz 10
(33, 'DOPASOWANIE',       'Dopasuj kraj do opisu jego flagi (kolory od góry do dołu).',   10,  1, NULL),
(34, 'DOPASOWANIE',       'Dopasuj kraj do kontynentu.',                                  10,  2, NULL);

-- ─────────────────────────────────────────────
-- PODTYPY PYTAŃ — pytanie_standard
-- ─────────────────────────────────────────────
INSERT INTO pytanie_standard (id, poprawna_odpowiedz, punkty) VALUES
(1,  'Paryż',                          1),
(2,  'Berlin',                         1),
(3,  'Madryt',                         1),
(4,  'Rzym',                           1),
(8,  'Bolesław Chrobry',               2),
(9,  '1944',                           2),
(24, 'Real Madryt',                    1),
(25, '11',                             1),
(26, 'Puchar Polski',                  1),
(29, 'HyperText Transfer Protocol',    2),
(30, 'C',                              1),
(31, 'Pamięć operacyjna',              1);

INSERT INTO standard_opcje (pytanie_id, opcja, opcja_order) VALUES
-- Q1 Paryż
(1, 'Paryż',      0), (1, 'Lyon',        1), (1, 'Marsylia',   2), (1, 'Bordeaux',    3),
-- Q2 Berlin
(2, 'Hamburg',    0), (2, 'Frankfurt',   1), (2, 'Berlin',     2), (2, 'Monachium',   3),
-- Q3 Madryt
(3, 'Barcelona',  0), (3, 'Madryt',     1), (3, 'Sewilla',    2), (3, 'Walencja',    3),
-- Q4 Rzym
(4, 'Mediolan',   0), (4, 'Neapol',     1), (4, 'Rzym',       2), (4, 'Florencja',   3),
-- Q8 Bolesław Chrobry
(8,  'Mieszko I',           0), (8,  'Bolesław Chrobry',  1), (8,  'Kazimierz Wielki',    2), (8,  'Władysław Łokietek', 3),
-- Q9 1944
(9,  '1939',     0), (9,  '1942',    1), (9,  '1944',      2), (9,  '1945',       3),
-- Q24 Real Madryt
(24, 'Real Madryt', 0), (24, 'FC Barcelona', 1), (24, 'Bayern Monachium', 2), (24, 'Manchester United', 3),
-- Q25 11
(25, '9',  0), (25, '10', 1), (25, '11', 2), (25, '12', 3),
-- Q26 Puchar Polski
(26, 'Ekstraklasa',   0), (26, 'Puchar Polski', 1), (26, 'Superpuchar', 2), (26, 'Liga Polska', 3),
-- Q29 HTTP
(29, 'HyperText Transfer Protocol', 0), (29, 'High Transfer Text Protocol', 1),
(29, 'HyperText Transmission Protocol', 2), (29, 'Hyper Terminal Text Protocol', 3),
-- Q30 C
(30, 'Python', 0), (30, 'Java', 1), (30, 'C', 2), (30, 'JavaScript', 3),
-- Q31 RAM
(31, 'Pamięć operacyjna', 0), (31, 'Karta graficzna', 1), (31, 'Procesor', 2), (31, 'Dysk twardy', 3);

-- ─────────────────────────────────────────────
-- PODTYPY PYTAŃ — pytanie_prawda_falsz
-- ─────────────────────────────────────────────
INSERT INTO pytanie_prawda_falsz (id, poprawna_odpowiedz, punkty) VALUES
(5,  true,  1),   -- Polska niepodległość 1918
(6,  true,  1),   -- Bitwa pod Wiedniem
(7,  false, 1),   -- NATO przed 1989
(32, false, 1);   -- Python nie jest kompilowany

-- ─────────────────────────────────────────────
-- PODTYPY PYTAŃ — pytanie_osobowosci
-- ─────────────────────────────────────────────
INSERT INTO pytanie_osobowosci (id) VALUES (10), (11), (12), (13), (27), (28);

INSERT INTO osobowosci_opcje (pytanie_id, opcja, wynik) VALUES
-- Quiz 3 - Q10
(10, 'Czytasz tutorial od deski do deski',         'Strateg'),
(10, 'Klikasz wszystko bez czytania',              'Action Hero'),
(10, 'Szukasz ukrytych easter eggów',              'Eksplorator'),
(10, 'Zapraszasz znajomych do wspólnej gry',       'Social Gracz'),
-- Quiz 3 - Q11
(11, 'Zaplanowanie idealnej strategii',            'Strateg'),
(11, 'Pokonanie trudnego bossa',                   'Action Hero'),
(11, 'Odkrycie sekretnego miejsca',                'Eksplorator'),
(11, 'Wygranie z innymi graczami',                 'Social Gracz'),
-- Quiz 3 - Q12
(12, 'Analizujesz co poszło nie tak',              'Strateg'),
(12, 'Grasz dalej bez zastanowienia',              'Action Hero'),
(12, 'Szukasz innej ścieżki w grze',               'Eksplorator'),
(12, 'Prosisz kogoś o pomoc',                      'Social Gracz'),
-- Quiz 3 - Q13
(13, 'Strategiczne (szachy, turowe)',               'Strateg'),
(13, 'Akcji i walki (FPS, beat ''em up)',           'Action Hero'),
(13, 'Open world i eksploracja',                   'Eksplorator'),
(13, 'Multiplayer i kooperacja',                   'Social Gracz'),
-- Quiz 8 - Q27
(27, 'Działasz od razu, bez zastanowienia',        'Baran'),
(27, 'Zachowujesz spokój i czekasz',               'Byk'),
-- Quiz 8 - Q28
(28, 'Aktywny wyjazd i nowe przygody',             'Baran'),
(28, 'Relaks w domu z książką lub filmem',         'Byk');

-- ─────────────────────────────────────────────
-- PODTYPY PYTAŃ — pytanie_dopasowanie
-- ─────────────────────────────────────────────
INSERT INTO pytanie_dopasowanie (id, punkty) VALUES
(14, 3),
(15, 3),
(33, 2),
(34, 3);

-- Lewa kolumna
INSERT INTO dopasowanie_lewa (pytanie_id, element, element_order) VALUES
(14, 'Thomas Edison',   0), (14, 'Alexander Bell', 1), (14, 'Bracia Wright',   2),
(15, 'Albert Einstein', 0), (15, 'Isaac Newton',   1), (15, 'Karol Darwin',    2),
(33, 'Polska',          0), (33, 'Niemcy',         1),
(34, 'Brazylia',        0), (34, 'Japonia',        1), (34, 'Egipt',          2);

-- Prawa kolumna
INSERT INTO dopasowanie_prawa (pytanie_id, element, element_order) VALUES
(14, 'Żarówka',                 0), (14, 'Telefon',             1), (14, 'Samolot',              2),
(15, 'Teoria względności',      0), (15, 'Teoria grawitacji',   1), (15, 'Teoria ewolucji',      2),
(33, 'Biały i czerwony',        0), (33, 'Czarny, czerwony, złoty', 1),
(34, 'Ameryka Południowa',      0), (34, 'Azja',                1), (34, 'Afryka',               2);

-- Poprawne pary
INSERT INTO dopasowanie_pary (pytanie_id, lewy, prawy) VALUES
(14, 'Thomas Edison',    'Żarówka'),
(14, 'Alexander Bell',   'Telefon'),
(14, 'Bracia Wright',    'Samolot'),
(15, 'Albert Einstein',  'Teoria względności'),
(15, 'Isaac Newton',     'Teoria grawitacji'),
(15, 'Karol Darwin',     'Teoria ewolucji'),
(33, 'Polska',           'Biały i czerwony'),
(33, 'Niemcy',           'Czarny, czerwony, złoty'),
(34, 'Brazylia',         'Ameryka Południowa'),
(34, 'Japonia',          'Azja'),
(34, 'Egipt',            'Afryka');

-- ─────────────────────────────────────────────
-- PODTYPY PYTAŃ — pytanie_uzupelnianie_luk
-- ─────────────────────────────────────────────
INSERT INTO pytanie_uzupelnianie_luk (id, punkty) VALUES
(16, 1),
(17, 1),
(18, 2);

INSERT INTO uzupelnianie_odpowiedzi (pytanie_id, odpowiedz, odpowiedz_order) VALUES
(16, 'damn',   0),
(17, 'Force',  0),
(18, 'be',     0),
(18, 'be',     1);

-- ─────────────────────────────────────────────
-- PODTYPY PYTAŃ — element_rankingu
-- ─────────────────────────────────────────────
INSERT INTO element_rankingu (id) VALUES (19), (20), (21), (22), (23);

-- ─────────────────────────────────────────────
-- KOMENTARZE
-- ─────────────────────────────────────────────
INSERT INTO komentarze (tresc, quiz_id, author_id, created_at) VALUES
('Świetny quiz! Nauczyłem się kilku stolic.',              1,  3, NOW()),
('Zbyt łatwy dla mnie, ale dobry dla początkujących.',     1,  4, NOW()),
('Trudniejszy niż myślałam, ale bardzo ciekawy!',          2,  5, NOW()),
('Brakuje pytań o II wojnę światową.',                     2,  2, NOW()),
('Dokładnie jestem Social Graczem haha :D',                3,  2, NOW()),
('Całkiem trafny wynik, polecam!',                         3,  3, NOW()),
('Nie wiedziałem, że Bell wynalazł telefon — myślałem że Edison!', 4, 5, NOW()),
('Klasyki filmowe — obowiązkowe!',                         5,  4, NOW()),
('Fajny pomysł na ranking planet, lubię takie formaty.',   6,  2, NOW()),
('Trochę za krótki, mógłby mieć więcej pytań.',            7,  3, NOW()),
('Wreszcie quiz o IT! Czekam na więcej z tej kategorii.',  9,  4, NOW()),
('Pytanie o HTTP było podchwytliwe.',                      9,  5, NOW());

-- ─────────────────────────────────────────────
-- POLUBIENIA
-- ─────────────────────────────────────────────
INSERT INTO user_quiz_likes (user_id, quiz_id) VALUES
(1,  1), (1,  4), (1,  9),
(2,  3), (2,  5), (2,  6),
(3,  1), (3,  2), (3,  7),
(4,  1), (4,  3), (4,  9),
(5,  2), (5,  4), (5,  6), (5, 7);
