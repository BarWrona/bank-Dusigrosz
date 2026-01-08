# 🏦 bank-Dusigrosz

**bank-Dusigrosz** to modularna aplikacja Java oparta na **Maven**, zaprojektowana jako przykład prostego systemu bankowego.  
Projekt może służyć jako baza do nauki architektury aplikacji, obsługi kont bankowych, transakcji oraz budowy REST API.

---

## 📂 Struktura projektu

Projekt jest podzielony na moduły:

```text
├── bootstrap/   # Punkt startowy aplikacji / konfiguracja
├── common/      # Wspólne klasy pomocnicze i utilsy
├── domain/      # Modele domenowe i encje
├── service/     # Logika biznesowa
├── web/         # Warstwa prezentacji / kontrolery REST
├── pom.xml      # Konfiguracja Maven i zależności
└── .mvn/        # Maven Wrapper


---

## 🚀 Funkcjonalności

- ✔️ Zarządzanie kontami bankowymi
- ✔️ Obsługa sald i operacji finansowych
- ✔️ Logika biznesowa oddzielona od warstwy web
- ✔️ Modularna architektura projektu
- ✔️ REST API gotowe do integracji z frontendem

---

## 🧠 Wymagania

Aby uruchomić projekt lokalnie, wymagane są:

- **Java 17+**
- **Maven 3.6+**
- (opcjonalnie) **IDE**: IntelliJ IDEA / VS Code / Eclipse

---

## ▶️ Uruchomienie projektu

### 1️⃣ Klonowanie repozytorium

```bash
git clone https://github.com/BarWrona/bank-Dusigrosz.git
cd bank-Dusigrosz
```
---

2️⃣ Budowanie projektu
```bash
mvn clean install
```
---
3️⃣ Uruchomienie aplikacji

Jeśli projekt jest oparty o Spring Boot:
```bash
mvn spring-boot:run
```
---
## 📡 REST API (przykładowe endpointy)

| Metoda | Endpoint             | Opis                       |
|--------|----------------------|----------------------------|
| GET    | `/accounts`          | Pobranie listy kont        |
| GET    | `/accounts/{id}`     | Szczegóły konta            |
| POST   | `/transactions`      | Wykonanie transakcji       |
| PUT    | `/accounts/{id}`     | Aktualizacja danych konta  |


Uwaga: Endpointy są przykładowe — dostosuj je do faktycznej implementacji.

🧪 Testy

Uruchomienie testów jednostkowych:
```bash
mvn test
```
---
## 🏗️ Architektura

Projekt wykorzystuje klasyczne podejście warstwowe:

| Moduł       | Opis                                           |
|------------|------------------------------------------------|
| **Domain** | Logika domenowa oraz encje aplikacji            |
| **Service**| Reguły biznesowe i przetwarzanie danych         |
| **Web**    | Kontrolery REST oraz API                        |
| **Common** | Współdzielone komponenty i klasy pomocnicze     |
| **Bootstrap** | Uruchamianie aplikacji oraz konfiguracja    |

