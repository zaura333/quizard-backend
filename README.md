# Quizard – Backend

Backend aplikacji webowej Quizard, umożliwiającej tworzenie i rozwiązywanie quizów. Projekt realizowany w ramach kursu **Programowanie Obiektowe**.

---

## Tech Stack

| Warstwa | Technologia |
|---|---|
| Język | Java 21 |
| Framework | Spring Boot 3.2 |
| Build | Maven |
| Baza danych | MySQL 8.0 |
| ORM | Spring Data JPA + Hibernate |
| Migracje | Flyway |
| Autentykacja | Spring Security + JWT (httpOnly cookie) |
| Konteneryzacja | Docker Compose |

---

## Wymagania

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (do uruchomienia przez Docker Compose)
- Java 21+ i Maven (do uruchomienia lokalnie bez Dockera)

---

## Uruchomienie

### Opcja 1 — Docker Compose (zalecane)

Uruchamia MySQL i backend razem:

```bash
docker compose up --build
```

Aplikacja dostępna pod: `http://localhost:8080`

Zatrzymanie:

```bash
docker compose down
```

Zatrzymanie i usunięcie danych z bazy:

```bash
docker compose down -v
```

---

### Opcja 2 — lokalnie (tylko backend, MySQL osobno)

1. Uruchom sam kontener MySQL:

```bash
docker compose up mysql -d
```

2. Uruchom backend przez Maven:

```bash
mvn spring-boot:run
```

Aplikacja dostępna pod: `http://localhost:8080`

---

## Konfiguracja

Konfiguracja w pliku `src/main/resources/application.properties`.

| Właściwość | Domyślna wartość | Opis |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/quizard` | URL bazy danych |
| `spring.datasource.username` | `quizard` | Użytkownik bazy |
| `spring.datasource.password` | `quizard` | Hasło bazy |
| `jwt.secret` | *(zmień przed wdrożeniem!)* | Sekret do podpisywania tokenów JWT |
| `jwt.expiration-ms` | `86400000` (24h) | Czas ważności tokenu JWT |
| `cors.allowed-origins` | `http://localhost:5173` | Dozwolony origin frontendu (Vite) |

> **Ważne:** przed wdrożeniem na serwer zmień `jwt.secret` na losowy ciąg min. 32 znaków.

---

## Struktura projektu

```
src/main/java/com/quizard/
├── auth/               # Rejestracja, logowanie, JWT
├── user/               # Model User, rola, kontroler /users
├── quiz/               # Hierarchia Quiz, serwis, kontroler /quizzes
├── question/           # Hierarchia Pytanie, interfejs Ocenialne
├── comment/            # Komentarze do quizów
├── attempt/            # Rozwiązywanie quizów, obliczanie wyników
└── common/
    ├── config/         # SecurityConfig (Spring Security + CORS)
    └── exception/      # GlobalExceptionHandler, wyjątki domenowe

src/main/resources/
├── application.properties
└── db/migration/
    └── V1__init.sql    # Schemat bazy danych (Flyway)
```

---

## API – przegląd endpointów

### Autentykacja

| Metoda | URL | Opis | Dostęp |
|---|---|---|---|
| `POST` | `/api/auth/register` | Rejestracja | Publiczny |
| `POST` | `/api/auth/login` | Logowanie (ustawia cookie JWT) | Publiczny |
| `POST` | `/api/auth/logout` | Wylogowanie (czyści cookie) | Zalogowany |

### Quizy

| Metoda | URL | Opis | Dostęp |
|---|---|---|---|
| `GET` | `/api/quizzes` | Lista quizów (filtry: `category`, `authorId`, `page`, `size`) | Publiczny |
| `GET` | `/api/quizzes/{id}` | Szczegóły quizu | Publiczny |
| `POST` | `/api/quizzes` | Utwórz quiz | Zalogowany |
| `PUT` | `/api/quizzes/{id}` | Edytuj quiz | Autor / ADMIN |
| `PUT` | `/api/quizzes/{id}/publish` | Opublikuj szkic | Autor / ADMIN |
| `DELETE` | `/api/quizzes/{id}` | Usuń quiz | Autor / ADMIN |
| `POST` | `/api/quizzes/{id}/attempt` | Rozwiąż quiz i pobierz wynik | Publiczny |
| `POST` | `/api/quizzes/{id}/like` | Polub quiz | Zalogowany |
| `DELETE` | `/api/quizzes/{id}/like` | Cofnij polubienie | Zalogowany |

### Komentarze

| Metoda | URL | Opis | Dostęp |
|---|---|---|---|
| `GET` | `/api/quizzes/{id}/comments` | Lista komentarzy | Publiczny |
| `POST` | `/api/quizzes/{id}/comments` | Dodaj komentarz | Zalogowany |
| `DELETE` | `/api/quizzes/{id}/comments/{commentId}` | Usuń komentarz | Autor komentarza / ADMIN |

### Użytkownik

| Metoda | URL | Opis | Dostęp |
|---|---|---|---|
| `GET` | `/api/users/me` | Dane zalogowanego użytkownika | Zalogowany |
| `DELETE` | `/api/users/me` | Usuń konto (soft delete) | Zalogowany |

---

## Typy quizów

| Typ (`quizType`) | Ocenianie | Limit czasu |
|---|:---:|:---:|
| `TEST_WIEDZY` | ✅ | ✅ opcjonalnie |
| `OSOBOWOSCI` | ❌ | ❌ |
| `DOPASOWANIA` | ✅ | ✅ opcjonalnie |
| `UZUPELNIANIE_LUK` | ✅ | ✅ opcjonalnie |
| `RANKING` | ❌ | ❌ |

---

## Typy pytań

| Typ (`pytanieType`) | Ocenianie | Opis |
|---|:---:|---|
| `STANDARD` | ✅ | Jedna poprawna odpowiedź spośród opcji |
| `MULTI_WYBOR` | ✅ | Wiele opcji, może być kilka poprawnych |
| `PRAWDA_FALSZ` | ✅ | Dwie opcje: `true` / `false` |
| `OSOBOWOSCI` | ✅* | Odpowiedź zbiera głos na kategorię wynikową |
| `DOPASOWANIE` | ✅ | Pary `lewy:prawy`, oddzielone przecinkami |
| `UZUPELNIANIE_LUK` | ✅ | Odpowiedzi CSV dla kolejnych luk |
| `ELEMENT_RANKINGU` | ❌ | Element do ułożenia w tierliście |

*quiz osobowości nie ma punktacji — zlicza głosy na wyniki

---

## Format odpowiedzi na pytania (`POST /api/quizzes/{id}/attempt`)

```json
{
  "startedAtEpochMs": 1715000000000,
  "answers": [
    { "questionId": 1, "answer": "Warszawa" },
    { "questionId": 2, "answer": "opcja1,opcja3" },
    { "questionId": 3, "answer": "true" },
    { "questionId": 4, "answer": "Francja:Paryż,Niemcy:Berlin" },
    { "questionId": 5, "answer": "słońce,księżyc" }
  ]
}
```

---

## Powiązane repozytoria

- **Frontend:** `quizard-frontend` (React + TypeScript + Vite + MUI)
