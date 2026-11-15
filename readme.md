# W.WAW

> **Zdążysz.** Bilety komunikacji miejskiej w Warszawie i dziennik opóźnień w jednym miejscu.

Full-stack MVP (temat: transport). Backend w Javie (Spring Boot), frontend w czystym HTML/CSS/JS, baza w pamięci (H2).

![Demo działania aplikacji](docs/demo.gif)

## Co potrafi aplikacja

- **Bilety:** katalog biletów ZTM (20-, 75-, 90-minutowy i dobowy, normalny i ulgowy), zamówienie, płatność BLIK lub kartą i wydanie biletów.
- **Skasowanie biletu:** kupiony bilet zaczyna obowiązywać dopiero po skasowaniu, wtedy rusza odliczanie ważności (jak w prawdziwych kasownikach).
- **Dziennik opóźnień:** pasażerowie zgłaszają opóźnienia linii. Aplikacja liczy średnie opóźnienie, 90. percentyl, najgorszą godzinę i ocenę A-D.
- **Automatyczne wygasanie zgłoszeń:** zgłoszenia znikają same **po miesiącu od dodania**. Opóźnienia zależą od pory dnia i roku, więc stare dane wprowadzałyby w błąd. Zgłoszeń nie da się usuwać ani edytować ręcznie.
- **Creative twist, doradca biletowy:** dobiera najtańszy bilet, który wystarczy na planowany przejazd **plus typowe opóźnienie wybranej linii** (90. percentyl ze zgłoszeń). Przejazd 15 minut punktualną linią to bilet 20-minutowy, ale linią spóźniającą się o 9 minut już 75-minutowy.
- **Katalog linii:** pełny CRUD (dodawanie, edycja, usuwanie).

## Opis techniczny

| Warstwa | Technologie |
|---|---|
| Backend | Java 21, Spring Boot 3.5 (Web, Data JPA, Validation), Maven |
| Baza danych | H2 in-memory (dane są tworzone i zasilane przy każdym starcie) |
| Frontend | HTML5, CSS, JavaScript (moduły ES), bez frameworków |
| Testy | JUnit 5, AssertJ |

**Struktura repozytorium**

```
backend/    API: controller / service / repository / dto / model / payment / exception / config
frontend/   statyczne strony (index, tickets, my-tickets, delays, lines) + css/ + js/
docs/       nagranie demo
AGENTS.md   instrukcje dla narzędzi AI
```

**Ciekawsze rozwiązania**

- **Retencja zgłoszeń.** Okres (domyślnie 30 dni) jest w `application.properties` (`wwaw.delays.retention-days`). Zadanie `@Scheduled` (`DelayCleanupJob`, codziennie o 3:00) usuwa stare rekordy. Dodatkowo każde zapytanie odfiltrowuje wygasłe zgłoszenia, więc nigdy nie są widoczne ani liczone w statystykach, nawet przed uruchomieniem zadania. Odpowiedź API zawiera `expiresAt`, dzięki czemu UI pokazuje, kiedy zgłoszenie zniknie.
- **Płatności za interfejsem.** `PaymentProvider` jest interfejsem, a `MockPaymentProvider` to symulacja (BLIK: dowolny 6-cyfrowy kod, `000000` oznacza odrzucenie; karta: zawsze zatwierdzona). Podłączenie prawdziwej bramki (np. Przelewy24) to jedna nowa klasa. Dane kart nie są przyjmowane ani przechowywane.
- **Dwuetapowy zakup.** `POST /orders` tworzy zamówienie `PENDING`, `POST /orders/{id}/payment` je opłaca i dopiero wtedy wydaje bilety. Odrzucona płatność zostawia zamówienie do ponowienia. `@Version` (blokada optymistyczna) chroni przed podwójną płatnością.
- **Czysta logika statystyk.** `StatsCalculator` nie zależy od Springa, więc jest łatwy do testowania jednostkowego.
- **Jednolity format błędów** (`ApiError`) z `@RestControllerAdvice`, walidacja Bean Validation z komunikatami po polsku, wspólny `Clock` do logiki zależnej od czasu.
- **Brak kont użytkowników.** Właściciel biletów to anonimowy identyfikator zapisany w `localStorage` przeglądarki. Uwierzytelnianie (np. JWT) byłoby naturalnym następnym krokiem.

**Dane demonstracyjne:** linie i zgłoszenia opóźnień są przykładowe (generowane przy starcie, część zgłoszeń wygaśnie w ciągu kilku dni). Ceny biletów odpowiadają taryfie krótkookresowej ZTM Warszawa: 20 min 3,40 zł, 75 min 4,40 zł, 90 min 7,00 zł, dobowy 15 zł (normalne) oraz połowa tych kwot (ulgowe). Aktualne ceny i trasy warto zweryfikować na [ztm.waw.pl](https://www.ztm.waw.pl).

## API (REST)

| Metoda | Ścieżka | Opis |
|---|---|---|
| GET, POST | `/api/lines` | lista linii, dodanie linii |
| GET, PUT, DELETE | `/api/lines/{id}` | szczegóły, edycja, usunięcie (usuwa też zgłoszenia linii) |
| GET | `/api/ticket-types?fare=NORMAL\|REDUCED` | katalog biletów |
| POST | `/api/orders` | utworzenie zamówienia (`PENDING`) |
| POST | `/api/orders/{id}/payment` | płatność (BLIK/karta), wydanie biletów |
| GET | `/api/tickets?ownerId=` | bilety użytkownika |
| POST | `/api/tickets/{id}/validate?ownerId=` | skasowanie biletu |
| GET, POST | `/api/delays` | ostatnie zgłoszenia (`?lineId=`), nowe zgłoszenie |
| GET | `/api/stats/lines`, `/api/lines/{id}/stats` | statystyki opóźnień |
| GET | `/api/advisor?lineId=&plannedMinutes=&fare=` | rekomendacja biletu |

Zgłoszenia opóźnień nie mają `PUT` ani `DELETE` celowo: wygasają automatycznie.

## Uruchomienie lokalnie

Wymagania: **JDK 21**, **Maven 3.9+** oraz dowolny prosty serwer plików statycznych (np. Python 3).

### 1. Backend (API)

```bash
cd backend
mvn spring-boot:run
```

API działa pod `http://localhost:8080/api`. Konsola H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:wwaw`, użytkownik `sa`, hasło puste). Testy: `mvn test`.

### 2. Frontend

W drugim terminalu:

```bash
cd frontend
python3 -m http.server 5500
```

Aplikacja: `http://localhost:5500`. Można też użyć `npx serve -l 5500` albo rozszerzenia Live Server w VS Code. Samo otwarcie pliku `index.html` z dysku nie zadziała (moduły ES wymagają serwera HTTP).

Adres API jest w `frontend/js/config.js`. CORS dopuszcza dowolny port na `localhost` i `127.0.0.1` (`wwaw.cors.allowed-origin-patterns` w `application.properties`).

### Jak przetestować płatność

W oknie zakupu wpisz dowolny 6-cyfrowy kod BLIK, a po zakupie skasuj bilet w zakładce „Moje bilety”. Kod `000000` pokaże odrzuconą płatność.

## Jak wspierałem się narzędziami AI

Wybrałem kierunek aplikacji: miała przypominać Jakdojade, z zakupem biletów i płatnościami, a zgłoszenia opóźnień miały znikać automatycznie po miesiącu. Nazwę W.WAW zaproponowałem sam. Sam zdecydowałem też o stosie (Java i Spring Boot, czysty JavaScript bez frameworków), palecie kolorów i minimalistycznym układzie strony.

Do realizacji użyłem Claude (claude.ai). Claude wygenerował szkielet backendu (warstwy, obsługę błędów, statystyki opóźnień, doradcę biletowego i symulowane płatności) oraz cały frontend, ponieważ moim głównym obszarem jest backend. Sam uruchamiałem i sprawdzałem działanie projektu, przeniosłem encje na Lombok, skonfigurowałem środowisko (WSL, JDK 21, Maven, Git) i opublikowałem repozytorium.

Instrukcje dla narzędzi AI są w pliku [`AGENTS.md`](AGENTS.md).
