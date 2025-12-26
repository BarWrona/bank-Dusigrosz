# 🏦 Spring Bank API – Advanced Finance Manager

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)](https://www.postgresql.org/)

**Spring Bank API** to zaawansowany system backendowy do zarządzania finansami osobistymi i wspólnymi. Aplikacja pozwala nie tylko na prowadzenie kont w różnych walutach, ale również na zarządzanie relacjami między użytkownikami a rachunkami oraz automatyczne przewalutowania w oparciu o dane rynkowe.

---

## ✨ Kluczowe Funkcjonalności (Features)

### 💳 Zaawansowane Zarządzanie Kontami
* **Model Multi-Currency:** Każdy użytkownik może posiadać wiele subkont w różnych walutach (PLN, EUR, USD, GBP).
* **Współdzielenie Rachunków (Multi-ownership):** System pozwala na przypisanie wielu użytkowników do jednego konta (idealne dla kont wspólnych/rodzinnych).
* **Profil Preferencji:** Każdy użytkownik posiada dedykowany profil z ustawieniami dotyczącymi przetwarzania danych osobowych, zgód marketingowych i preferencji powiadomień.

### 💸 System Transakcyjny i Kantor
* **Przelewy Międzywalutowe:** Możliwość wykonywania przelewów np. z konta EUR na konto USD.
* **Integracja z NBP API:** System automatycznie pobiera aktualne kursy średnie z tabeli A Narodowego Banku Polskiego, aby precyzyjnie przeliczać kwoty podczas transferów.



---

## 🛠 Stos Technologiczny (Tech Stack)

* **Język:** Java 17+
* **Framework:** Spring Boot 3.x
* **Baza Danych:** PostgreSQL (Relacyjna struktura dla zapewnienia spójności danych)
* **Dostęp do danych:** Spring Data JPA / Hibernate
* **Komunikacja API:** NBP WebClient / RestTemplate
* **Zarządzanie migracjami:** Flyway lub Liquibase (zalecane)
* **Dokumentacja:** Swagger UI (OpenAPI)

---

## 🏗 Schemat Relacji (Domain Model)

Projekt opiera się na zaawansowanym modelu bazodanowym:
1.  **User ↔ Profile:** Relacja One-to-One (Ustawienia i zgody).
2.  **User ↔ Account:** Relacja Many-to-Many (Użytkownik może mieć wiele kont, konto może mieć wielu właścicieli).
3.  **Account ↔ Transaction:** Historia operacji i przewalutowań.

---

## 🚀 Instalacja i Uruchomienie

1.  **Skonfiguruj PostgreSQL:**
    Utwórz bazę danych i zaktualizuj plik `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/bank_db
    spring.datasource.username=twoj_user
    spring.datasource.password=twoje_haslo
    ```

2.  **Pobierz i zbuduj projekt:**
    ```bash
    git clone [https://github.com/TwojLogin/spring-bank-api.git](https://github.com/TwojLogin/spring-bank-api.git)
    cd spring-bank-api
    ./mvnw clean install
    ```

3.  **Uruchom aplikację:**
    ```bash
    ./mvnw spring-boot:run
    ```

---

## 📊 Przykładowe Endpointy

| Metoda | Endpoint | Opis |
| :--- | :--- | :--- |
| `POST` | `/api/users` | Rejestracja użytkownika wraz z profilem preferencji |
| `GET` | `/api/accounts/my` | Lista wszystkich kont zalogowanego użytkownika |
| `POST` | `/api/transfers/exchange` | Przelew między kontami z automatycznym przewalutowaniem |
| `GET` | `/api/currency/rates` | Pobranie aktualnych kursów walut pobranych z NBP |

---

## 🛤 Mapa drogowa (Roadmap)

- [x] Implementacja modelu użytkownika i kont wielowalutowych.
- [x] Integracja z NBP API.
- [ ] Implementacja logiki przelewów cross-currency.
- [ ] Obsługa wielu właścicieli (zaproszenia do konta).
- [ ] System autentykacji (Spring Security + JWT).
- [ ] Konteneryzacja aplikacji (Docker).

---
*Projekt rozwijany w celach edukacyjnych, demonstrujący obsługę złożonych relacji bazodanowych i integracji zewnętrznych API.*
