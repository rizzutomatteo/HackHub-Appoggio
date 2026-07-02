# HackHub — Porting Spring Boot

Porting a **Spring Boot** del progetto universitario **HackHub** (UNICAM, corso di
Ingegneria del Software / IDS), una piattaforma web per la gestione di hackathon.

## Cos'è

È la reimplementazione del progetto HackHub come applicazione Spring Boot, mantenendo
l'architettura **EBC (Entity–Boundary–Control)** vista a lezione e mappandola sugli
stereotipi di Spring:

| Ruolo EBC / corso                | Realizzazione Spring Boot                                        |
| -------------------------------- | ---------------------------------------------------------------- |
| **Boundary** (interfaccia REST)  | `@RestController` nel package `boundary` (+ `@ControllerAdvice`) |
| **Control** (logica applicativa) | `@Service` nel package `service`                                 |
| **Entity** (dominio)             | classi nel package `entity`                                      |
| **Repository** (persistenza)     | `@Repository` in-memory (HashMap) nel package `repository`       |

I **design pattern** del progetto originale sono conservati:

- **State** (`package state`): lo stato dell'hackathon (`InIscrizione`, `InCorso`,
  `InValutazione`, `Concluso`) è modellato come macchina a stati.
- **Observer** (`package observer`): le notifiche agli utenti (`Subject` / `Observer`).
- **Adapter** (`package adapter`): l'integrazione con i due sistemi esterni (Google
  Calendar e Sistema di Pagamento) dietro le interfacce `Calendar` e `SistemaPagamento`,
  con implementazione reale o fallback in-memory scelta in `config/EsterniConfig`.

### Stack tecnologico

- **Spring Boot 4.1** (Web MVC, Tomcat embedded)
- **Java 25** (toolchain Gradle)
- **Gradle** (Gradle Wrapper incluso)
- **Repository in-memory** (`HashMap`), **nessun database** — coerente con lo stile
  delle lezioni del corso
- Librerie dei sistemi esterni: `google-api-services-calendar`, `stripe-java`

## Come si costruisce e si avvia

Dalla cartella `springboot/`:

```bash
cd springboot

# Avvio diretto in sviluppo
./gradlew bootRun

# Oppure: build del jar eseguibile e avvio
./gradlew bootJar
java -jar build/libs/hackhub-springboot-0.0.1-SNAPSHOT.jar
```

- Server REST: **http://localhost:8080**
- Client demo (pagina web): **http://localhost:8080/**

Il **client demo** (`src/main/resources/static/index.html` + `app.js`) è un semplice
client HTML + JavaScript (`fetch`) con pulsanti che eseguono il flusso end-to-end
(registra 2 utenti → crea hackathon → crea team → iscrivi team → richiedi supporto →
proponi call → **paga il montepremi**) e mostra le risposte JSON.

## Integrazioni con sistemi esterni (pattern Adapter)

Entrambe le integrazioni sono **opzionali**: senza configurazione l'app parte comunque
usando un **fallback in-memory** (nessuna chiamata di rete). La scelta reale/fallback è
in `config/EsterniConfig` ed è guidata dalle property in
`src/main/resources/application.properties`.

### 1. Google Calendar — UC13 Proponi Call / UC14 Accetta Proposta

Configurazione in `application.properties`:

```properties
google.calendar.credentials=<path al file JSON del service account>
google.calendar.id=<id calendario: "primary" o l'indirizzo del calendario condiviso>
```

Passi per abilitare l'integrazione reale:

1. **Google Cloud Console** → abilita l'API **"Google Calendar API"**.
2. Crea un **Service Account**.
3. Crea per il service account una **chiave JSON** e scaricala; metti il suo path in
   `google.calendar.credentials`.
4. **Condividi il calendario** Google con l'email del service account, assegnando il
   permesso **"Apportare modifiche agli eventi"**.

Senza credenziali valide l'app usa il fallback `CalendarInMemory` (nessuna rete) e parte
ugualmente.

### 2. Sistema di Pagamento — Stripe (modalità TEST) — UC16 Paga Montepremi

Configurazione in `application.properties`:

```properties
stripe.api-key=sk_test_...
```

Passi per abilitare l'integrazione reale (in **test**, senza addebiti veri):

1. **dashboard.stripe.com** → attiva **"Test mode"**.
2. **Developers → API keys** → copia la **Secret key** che inizia con `sk_test_...`.
3. Incolla la key in `stripe.api-key`.

Con la test key **nessun addebito reale**; la carta di test `pm_card_visa` va a buon
fine. Senza key l'app usa il fallback `SistemaPagamentoInMemory`.

## Elenco endpoint REST (con mappatura ai casi d'uso)

Base URL: `http://localhost:8080`. Le date sono in formato **ISO** (`yyyy-MM-dd`;
gli slot delle call sono ISO datetime, es. `2026-09-02T15:00:00`).

### Account (`/api/account`)

| Metodo | Endpoint                  | Body                    | UC                  |
| ------ | ------------------------- | ----------------------- | ------------------- |
| POST   | `/api/account/registrati` | `{nome,email,password}` | UC19 Registrazione  |
| POST   | `/api/account/accedi`     | `{email,password}`      | UC20 Autenticazione |

### Hackathon (`/api/hackathon`)

| Metodo | Endpoint                            | Body / Note                                                                                                                                    | UC                                      |
| ------ | ----------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------- |
| POST   | `/api/hackathon`                    | `{nome,dataInizio,dataFine,luogo,regolamento,scadenzaIscrizioni,dimensioneMaxTeam,montepremi,organizzatoreEmail,giudiceEmail,mentoriEmails[]}` | UC01 Crea Hackathon                     |
| GET    | `/api/hackathon/pubblici`           | —                                                                                                                                              | UC21 Visualizza Hackathon Pubblici      |
| POST   | `/api/hackathon/{nome}/avvia`       | —                                                                                                                                              | UC06 Avvia Hackathon                    |
| POST   | `/api/hackathon/inizia-valutazione` | —                                                                                                                                              | UC07 Inizia Valutazione                 |
| POST   | `/api/hackathon/{nome}/proclama`    | `{teamVincitore}`                                                                                                                              | UC08 Proclama Vincitore                 |
| POST   | `/api/hackathon/{nome}/paga`        | —                                                                                                                                              | UC16 Paga Montepremi (Adapter → Stripe) |
| POST   | `/api/hackathon/{nome}/mentori`     | `{organizzatoreEmail,utenteEmail}`                                                                                                             | UC22 Aggiungi Mentore                   |

### Team, iscrizioni e inviti (`/api/team`)

| Metodo | Endpoint                        | Body                    | UC                  |
| ------ | ------------------------------- | ----------------------- | ------------------- |
| POST   | `/api/team`                     | `{nome,creatoreEmail}`  | UC04 Crea Team      |
| POST   | `/api/team/{nome}/iscrivi`      | `{hackathon}`           | UC05 Iscrivi Team   |
| POST   | `/api/team/{nome}/inviti`       | `{utenteInvitatoEmail}` | UC09 Invita Membro  |
| POST   | `/api/team/inviti/{id}/accetta` | —                       | UC10 Accetta Invito |

### Sottomissioni (`/api/sottomissioni`)

| Metodo | Endpoint                              | Body                              | UC                                 |
| ------ | ------------------------------------- | --------------------------------- | ---------------------------------- |
| POST   | `/api/sottomissioni`                  | `{iscrizioneId,titolo,contenuto}` | UC02 Sottometti Progetto           |
| GET    | `/api/sottomissioni/hackathon/{nome}` | —                                 | UC02/UC03 Visualizza Sottomissioni |
| POST   | `/api/sottomissioni/{id}/valuta`      | `{giudizio,punteggio}`            | UC03 Valuta Sottomissione          |

### Mentoring: supporto, call e segnalazioni (`/api/mentoring`)

| Metodo | Endpoint                                    | Body                                                  | UC                                                |
| ------ | ------------------------------------------- | ----------------------------------------------------- | ------------------------------------------------- |
| POST   | `/api/mentoring/richieste`                  | `{teamNome,hackathonNome,messaggio}`                  | UC11 Richiede Supporto                            |
| POST   | `/api/mentoring/call`                       | `{mentoreEmail,richiestaId,slot}` (slot ISO datetime) | UC13 Propone Call (Adapter → Google Calendar)     |
| POST   | `/api/mentoring/call/{id}/accetta`          | —                                                     | UC14 Accetta Proposta (Adapter → Google Calendar) |
| POST   | `/api/mentoring/call/{id}/rifiuta`          | —                                                     | UC15 Rifiuta Proposta                             |
| POST   | `/api/mentoring/segnalazioni`               | `{mentoreEmail,teamNome,hackathonNome,descrizione}`   | UC17 Segnala Team                                 |
| POST   | `/api/mentoring/segnalazioni/{id}/gestisci` | `{decisione}`                                         | UC18 Gestisci Segnalazione                        |

> Nota: la lettura delle richieste di supporto del Mentore (UC12) e delle segnalazioni
> dell'Organizzatore (UC18 in sola lettura) è coperta lato control tramite lo scope per
> `Incarico`; gli endpoint di scrittura principali sono quelli elencati sopra.

### Gestione degli errori

Tutti i controller condividono `boundary/RestExceptionHandler` (`@ControllerAdvice`):

- `IllegalArgumentException` (dati o stato non validi lanciati dai service) → **400 Bad
  Request** con corpo `{"errore": "<messaggio>"}`;
- qualsiasi altra eccezione → **500 Internal Server Error** con `{"errore": "<messaggio>"}`.

## Nota sulla persistenza

I repository sono **in-memory** (`HashMap`), quindi **non c'è alcun database**: i dati
vivono finché l'applicazione è in esecuzione. È una scelta coerente con lo stile del
corso (focus su architettura e design pattern). L'introduzione di una **persistenza su
DB** (es. Spring Data JPA) è una naturale **evoluzione futura**: essendo l'accesso ai
dati già isolato dietro le interfacce `Repo...`, basterebbe fornirne implementazioni su
database senza toccare il control né il boundary.
