# Casi d'Uso Dettagliati — Prima Iterazione HackHub

## 1. Scopo e come usare questo documento con Visual Paradigm 17.3

Questo file contiene la **specifica in formato dettagliato** ("fully-dressed", stile Larman — cfr. esempio _Elabora Vendita_ in `docs/UML_02_CasiUso.pdf` slide 26-30) dei casi d'uso del **percorso end-to-end** della prima iterazione, scelti in `ITERAZIONE_1_CASI_USO.md`:

| ID   | Caso d'uso           | Attore primario |
| ---- | -------------------- | --------------- |
| UC01 | Crea Hackathon       | Organizzatore   |
| UC02 | Crea Team            | Utente          |
| UC03 | Iscrive Team         | Membro del team |
| UC04 | Invia Sottomissione  | Membro del team |
| UC05 | Valuta Sottomissione | Giudice         |

`Registrazione` e `Autenticazione` (attore Visitatore) non sono specificati in dettaglio: compaiono come **pre-condizione trasversale** di ogni caso d'uso autenticato (vedi §4).

### 1.1 Mappatura sezione → campo di Visual Paradigm

In VP: seleziona il caso d'uso nel diagramma → tasto destro → **Open Use Case Details** (oppure doppio clic nella vista UeXceler). La descrizione breve sta invece in **Open Specification ▸ Documentation**.

| Sezione di questo documento                      | Dove inserirla in Visual Paradigm 17.3                    |
| ------------------------------------------------ | --------------------------------------------------------- |
| Nome                                             | Nome del caso d'uso                                       |
| ID                                               | Prefisso nel nome (`UC01 …`) **oppure** un _Tagged Value_ |
| Portata (Scope)                                  | _Documentation_                                           |
| Livello                                          | _Use Case Details ▸ Details ▸ Level_                      |
| Rank / Complessità / Stato                       | _Basic Information ▸ Rank_; _Details ▸ Complexity/Status_ |
| Breve descrizione                                | _Open Specification ▸ Documentation_                      |
| Attore primario                                  | _Basic Information ▸ Primary Actors_                      |
| Attori secondari / di supporto                   | _Basic Information ▸ Supporting Actors_                   |
| Parti interessate e interessi                    | _Documentation_ oppure _Details ▸ Assumptions_            |
| Pre-condizioni                                   | _Details ▸ Pre-conditions_                                |
| Post-condizioni (garanzia di successo)           | _Details ▸ Post-conditions_                               |
| Scenario principale (flusso base)                | _Flow of Events ▸ Basic Flow_ (passi numerati)            |
| Estensioni / flussi alternativi                  | _Flow of Events ▸ Extensions_                             |
| Regole di business                               | _Requirements_ (collega un Requirement) o _Documentation_ |
| Requisiti speciali / Frequenza / Problemi aperti | _Details ▸ Assumptions_ / _Documentation_                 |

### 1.2 Strumenti di Visual Paradigm 17.3 da sfruttare

- **Flow of Events**: inserisci i passi del flusso base uno per riga (VP li numera da solo). Per le ramificazioni usa gli operatori **if / while / jump**; per richiamare un caso d'uso incluso usa **"Referenced Use Case"** (utile per il rimando ad `Autenticazione`).
- **Generazione automatica**: dal Flow of Events VP può generare uno **scheletro di Sequence Diagram** o di **Activity Diagram** — comodo per passare dalla specifica alla progettazione delle interazioni.
- **Requirements**: crea un Requirement per ogni regola di business (es. _"punteggio 0-10"_) e collegalo al caso d'uso, così la regola è tracciabile.
- **Rank / Complexity / Use Case Status / Implementation Status**: usali per la prioritizzazione e per marcare lo stato di avanzamento nell'iterazione.
- **Wireframe** e **Sub-Diagrams**: opzionali, per abbozzare l'interfaccia o dettagliare un passo.
- **Test Plan**: puoi derivare i casi di test dalle estensioni (un test per ogni flusso alternativo).

### 1.3 Convenzioni di scrittura (cfr. `UML_02_CasiUso.pdf` slide 13-15)

- Passi **numerati**, in **forma attiva**: `N. Il <attore/sistema> <azione>` (mai forma passiva).
- Il **primo passo** è sempre un'azione dell'attore primario (attivazione).
- Ramificazioni con la parola **"Se"** e indici annidati (`x.1`, `x.2`).
- **Estensioni** etichettate con il numero del passo + lettera (`6a`, `6b`); `*a` = in qualunque momento; un'estensione non dovrebbe avere a sua volta estensioni.

### 1.4 Ciclo di vita dell'hackathon in questa iterazione

In questa iterazione **non esistono casi d'uso dedicati alle transizioni di stato**: gli stati avanzano **automaticamente, in base alle date** definite alla creazione (comportamento interno del sistema). Questo rende la fetta dei 5 casi d'uso **eseguibile end-to-end** senza casi d'uso aggiuntivi.

| Transizione                       | Quando avviene (automatica)                                       |
| --------------------------------- | ----------------------------------------------------------------- |
| (creazione) → **in iscrizione**   | alla creazione dell'hackathon (UC01)                              |
| **in iscrizione** → **in corso**  | al raggiungimento della _scadenza iscrizioni_                     |
| **in corso** → **in valutazione** | al raggiungimento della _data di fine_ (= scadenza sottomissioni) |
| **in valutazione** → **concluso** | fuori iterazione (dipende da _Proclama Vincitore_)                |

> In iterazioni successive queste transizioni potranno diventare **casi d'uso espliciti** (es. `Avvia Hackathon`, `Inizia Fase Valutazione`, attore Organizzatore) o essere affidate a un attore **Tempo**/scheduler, rendendo logica e flusso più ricchi.

---

## 2. Specifiche dei casi d'uso

### UC01 — Crea Hackathon

| Campo                  | Valore                         |
| ---------------------- | ------------------------------ |
| **Nome**               | Crea Hackathon                 |
| **ID**                 | UC01                           |
| **Portata**            | HackHub (sistema)              |
| **Livello**            | Obiettivo utente               |
| **Attore primario**    | Organizzatore                  |
| **Attori di supporto** | nessuno (in questa iterazione) |
| **Rank / Complessità** | Alta / Media                   |

**Breve descrizione.** L'Organizzatore crea un nuovo hackathon definendone le informazioni essenziali; alla creazione l'hackathon entra nello stato **"in iscrizione"** e diventa visibile nell'elenco.

**Evento scatenante.** L'Organizzatore decide di pubblicare un nuovo hackathon.

**Parti interessate e interessi.**

- _Organizzatore_: vuole pubblicare un evento corretto e completo.
- _Membri del team / Utenti_: vogliono trovare l'hackathon e iscriversi.
- _Giudice e Mentori_: vogliono risultare assegnati all'evento.

**Pre-condizioni.** L'attore è autenticato e ha il ruolo di **Organizzatore** (membro dello staff). _(rimando: Autenticazione, §4)_

**Post-condizioni (garanzia di successo).**

- Esiste un nuovo hackathon persistito nello stato **"in iscrizione"**.
- L'hackathon ha esattamente **un Giudice** e **almeno un Mentore** associati.
- L'hackathon è elencato tra quelli consultabili.

**Scenario principale di successo.**

1. L'Organizzatore richiede la creazione di un nuovo hackathon.
2. Il sistema presenta il modulo dei dati dell'hackathon.
3. L'Organizzatore inserisce nome, regolamento, scadenza iscrizioni, data di inizio, data di fine, luogo, premio in denaro e dimensione massima del team.
4. L'Organizzatore seleziona un Giudice e uno o più Mentori tra gli utenti/staff disponibili.
5. L'Organizzatore conferma la creazione.
6. Il sistema valida i dati inseriti.
7. Il sistema crea l'hackathon nello stato "in iscrizione" e lo salva.
8. Il sistema mostra il riepilogo dell'hackathon creato.

**Estensioni (flussi alternativi).**

- **4a. Nessun Giudice selezionato (o più di uno) / nessun Mentore:**
  - 4a.1 Il sistema impedisce la conferma e segnala il vincolo ("un Giudice e almeno un Mentore").
- **6a. Dati mancanti o non validi** (es. scadenza iscrizioni successiva alla data di inizio, data di fine precedente all'inizio, premio negativo, dimensione team < 1):
  - 6a.1 Il sistema segnala i campi non validi.
  - 6a.2 Il sistema torna al passo 3.
- **\*a. In qualunque momento l'Organizzatore annulla:** nessun hackathon viene creato.

**Regole di business esercitate.**

- Alla creazione l'hackathon entra nel primo stato del ciclo di vita: **"in iscrizione"**.
- Coerenza temporale: `scadenza iscrizioni ≤ data inizio ≤ data fine`.
- Composizione staff: **esattamente un Giudice**, **almeno un Mentore** (cfr. `progetto.MD` §Organizzatore).

**Requisiti speciali / note implementative.** Validazione lato server; il premio è un importo monetario (valuta + importo ≥ 0); l'aggiunta di Mentori successiva alla creazione è un caso d'uso distinto (rinviato).

**Frequenza d'uso.** Bassa (solo Organizzatori).

**Problemi aperti.** _Risolto:_ la scadenza delle sottomissioni **coincide con la data di fine** dell'hackathon, cioè con l'ingresso nello stato "in valutazione" (vedi §1.4 e UC04). Resta da definire se il luogo possa essere "online".

**Suggerimento VP.** Inserisci i passi 1-8 nel _Flow of Events ▸ Basic Flow_; le estensioni `4a`/`6a`/`*a` nella sezione _Extensions_. Crea un Requirement "Stato iniziale = in iscrizione" e collegalo.

---

### UC02 — Crea Team

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Crea Team         |
| **ID**                 | UC02              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Utente            |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Alta / Bassa      |

**Breve descrizione.** Un Utente registrato crea un nuovo team, invitando eventualmente altri utenti. Chi crea il team ne diventa il primo membro (e amministratore).

**Evento scatenante.** L'Utente decide di formare una squadra per partecipare agli hackathon.

**Parti interessate e interessi.**

- _Utente creatore_: vuole formare la propria squadra per partecipare.
- _Utenti invitati_: vogliono ricevere l'invito al team.

**Pre-condizioni.** L'Utente è autenticato e **non appartiene già ad alcun team**. _(rimando: Autenticazione, §4)_

**Post-condizioni (garanzia di successo).**

- Esiste un nuovo team persistito.
- L'Utente creatore risulta membro (ed amministratore) del team.
- Gli eventuali inviti agli altri utenti sono stati registrati/inviati.

**Scenario principale di successo.**

1. L'Utente richiede la creazione di un nuovo team.
2. Il sistema verifica che l'Utente non appartenga già ad un team.
3. Il sistema presenta il modulo (nome del team ed elenco di utenti da invitare).
4. L'Utente inserisce il nome del team e seleziona gli utenti da invitare.
5. L'Utente conferma.
6. Il sistema crea il team e vi associa l'Utente come membro/amministratore.
7. Il sistema invia gli inviti agli utenti selezionati.
8. Il sistema conferma la creazione del team.

**Estensioni (flussi alternativi).**

- **2a. L'Utente appartiene già ad un team:**
  - 2a.1 Il sistema nega l'operazione e informa l'Utente (invariante "un solo team").
- **4a. Nome del team mancante o già in uso:**
  - 4a.1 Il sistema segnala l'errore e torna al passo 4.
- **\*a. In qualunque momento l'Utente annulla:** nessun team viene creato.

**Regole di business esercitate.**

- **Un utente può appartenere a un solo team alla volta** (cfr. `progetto.MD` §Utente) — verificata al passo 2.
- Nome del team univoco (assunzione).

**Requisiti speciali / note implementative.** L'**accettazione di un invito** è un caso d'uso separato (`Accetta Invito`, attore Utente); anche al momento dell'accettazione va riverificato l'invariante "un solo team". In questa iterazione `Crea Team` può essere implementato anche in forma minimale (creatore = unico membro iniziale), rinviando il flusso completo degli inviti.

**Frequenza d'uso.** Media (una volta per partecipante per evento).

**Problemi aperti.** L'attore "Amministratore Team" non è citato esplicitamente in `progetto.MD`: va dichiarato nei requisiti (vedi `REVISIONE_USE_CASE.md` §1.2).

**Suggerimento VP.** Il passo 7 ("invia gli inviti") può rimandare al caso d'uso `Invita Membro` tramite _Referenced Use Case_ nel Flow of Events.

---

### UC03 — Iscrive Team

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Iscrive Team      |
| **ID**                 | UC03              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Membro del team   |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Alta / Bassa      |

**Breve descrizione.** Un Membro del team iscrive il proprio team a un hackathon che si trova nello stato "in iscrizione". È il passo che lega Team↔Hackathon e che abilita la sottomissione (UC04).

**Evento scatenante.** Il Membro del team vuole far partecipare la propria squadra a un hackathon aperto alle iscrizioni.

**Parti interessate e interessi.**

- _Membro del team_: vuole far partecipare la propria squadra all'evento.
- _Organizzatore_: vuole l'elenco dei team partecipanti.

**Pre-condizioni.**

- L'attore è autenticato ed è **membro di un team**.
- Esiste un hackathon nello stato **"in iscrizione"**.
- Il team **non è già iscritto** a quell'hackathon.

**Post-condizioni (garanzia di successo).** Il team risulta iscritto all'hackathon (relazione Team↔Hackathon registrata).

**Scenario principale di successo.**

1. Il Membro del team consulta l'elenco degli hackathon.
2. Il Membro del team seleziona un hackathon nello stato "in iscrizione".
3. Il Membro del team richiede l'iscrizione del proprio team.
4. Il sistema verifica che l'hackathon sia ancora **in iscrizione** (iscrizioni aperte) e che il team non sia già iscritto.
5. Il sistema registra l'iscrizione del team all'hackathon.
6. Il sistema conferma l'iscrizione.

**Estensioni (flussi alternativi).**

- **4a. L'hackathon non è più nello stato "in iscrizione" (iscrizioni chiuse):**
  - 4a.1 Il sistema rifiuta l'iscrizione e ne spiega il motivo.
- **4b. Il team è già iscritto a quell'hackathon:**
  - 4b.1 Il sistema informa l'attore e non crea un duplicato.
- **4c. (opzionale) Il numero di membri del team supera la dimensione massima dell'hackathon:**
  - 4c.1 Il sistema segnala il superamento del limite.

**Regole di business esercitate.** L'iscrizione è consentita **solo mentre l'hackathon è nello stato "in iscrizione"** (le iscrizioni si chiudono automaticamente al raggiungimento della scadenza iscrizioni — vedi §1.4).

**Requisiti speciali / note implementative.** Decidere se l'iscrizione è riservata all'Amministratore del team o consentita a qualunque membro (cfr. `REVISIONE_USE_CASE.md` §2.3). Operazione idempotente (no doppia iscrizione).

**Frequenza d'uso.** Media.

**Problemi aperti.** Verifica della dimensione massima del team: bloccante o solo avviso?

**Suggerimento VP.** L'esito di UC03 è la **pre-condizione di UC04**: documenta questo concatenamento nelle _Pre-conditions_ di UC04.

---

### UC04 — Invia Sottomissione

| Campo                  | Valore              |
| ---------------------- | ------------------- |
| **Nome**               | Invia Sottomissione |
| **ID**                 | UC04                |
| **Portata**            | HackHub (sistema)   |
| **Livello**            | Obiettivo utente    |
| **Attore primario**    | Membro del team     |
| **Attori di supporto** | nessuno             |
| **Rank / Complessità** | Alta / Media        |

**Breve descrizione.** Un Membro del team invia la sottomissione del proprio team per un hackathon a cui il team è iscritto. Lo stesso caso d'uso copre **sia il primo invio sia gli aggiornamenti successivi**: la sottomissione resta modificabile finché l'hackathon è "in corso".

**Evento scatenante.** Il team ha un lavoro (o un suo aggiornamento) da consegnare per l'hackathon.

**Parti interessate e interessi.**

- _Membro del team_: vuole consegnare il lavoro entro la scadenza e poterlo aggiornare.
- _Giudice_: vuole una sottomissione completa da valutare (UC05).

**Pre-condizioni.**

- L'attore è autenticato ed è membro di un team **iscritto** all'hackathon (post-condizione di UC03).
- L'hackathon è nello stato **"in corso"** (le sottomissioni sono aperte).

**Post-condizioni (garanzia di successo).** La sottomissione del team per quell'hackathon è creata (o aggiornata) e persistita con data-ora, associata a team + hackathon.

**Scenario principale di successo.**

1. Il Membro del team seleziona l'hackathon a cui il proprio team è iscritto.
2. Il Membro del team richiede l'invio della sottomissione.
3. Il sistema verifica che l'hackathon sia ancora nello stato **"in corso"**.
4. Il Membro del team inserisce i contenuti della sottomissione (es. titolo, descrizione, collegamento/artefatti).
5. Il Membro del team conferma l'invio.
6. Il sistema registra la sottomissione con la data-ora corrente.
7. Il sistema conferma l'avvenuto invio.

**Estensioni (flussi alternativi).**

- **3a. L'hackathon è passato a "in valutazione" (sottomissioni chiuse):**
  - 3a.1 Il sistema rifiuta l'invio/aggiornamento e informa che il termine è scaduto.
- **6a. Esiste già una sottomissione del team per quell'hackathon:**
  - 6a.1 Il sistema **aggiorna** la sottomissione esistente (consentito perché l'hackathon è ancora "in corso") e ne aggiorna la data-ora.
- **\*a. In qualunque momento il Membro del team annulla:** la sottomissione non viene modificata.

**Regole di business esercitate.**

- **Invio e aggiornamento consentiti solo mentre l'hackathon è "in corso"** (cfr. `progetto.MD` §Membro del Team: "inviare la sottomissione entro la scadenza prevista. Fino a tale scadenza il membro può ancora aggiornare la sottomissione"). La scadenza coincide con l'ingresso in "in valutazione" (vedi §1.4).
- Una sola sottomissione per team per hackathon (assunzione).

**Requisiti speciali / note implementative.** Controllo dello stato **lato server** (non fidarsi del client); operazione transazionale; conservare la data-ora dell'ultimo aggiornamento.

**Frequenza d'uso.** Alta durante la fase "in corso".

**Problemi aperti.** _Risolto:_ la chiusura delle sottomissioni coincide con il passaggio automatico a "in valutazione" (= data di fine, vedi §1.4); non serve un campo "scadenza sottomissioni" separato.

**Suggerimento VP.** Modella il ramo `6a` (crea-o-aggiorna) con un operatore **if** nel Flow of Events; valuta di generare un **Sequence Diagram** da questo flusso, è il caso d'uso con più logica.

> **Nota sul diagramma.** In `useCase.jpg` l'aggiornamento della sottomissione è coperto da questo caso d'uso (estensione 6a) e il caso d'uso separato `Modifica Sottomissioni` **è stato rimosso** dal diagramma.

---

### UC05 — Valuta Sottomissione

| Campo                  | Valore               |
| ---------------------- | -------------------- |
| **Nome**               | Valuta Sottomissione |
| **ID**                 | UC05                 |
| **Portata**            | HackHub (sistema)    |
| **Livello**            | Obiettivo utente     |
| **Attore primario**    | Giudice              |
| **Attori di supporto** | nessuno              |
| **Rank / Complessità** | Alta / Media         |

**Breve descrizione.** Il Giudice assegnato all'hackathon valuta ciascuna sottomissione con un **giudizio scritto** e un **punteggio compreso tra 0 e 10**.

**Evento scatenante.** L'hackathon entra nella fase di valutazione e il Giudice deve esaminare le sottomissioni.

**Parti interessate e interessi.**

- _Giudice_: vuole registrare valutazioni valide e coerenti.
- _Membri del team_: vogliono ricevere giudizio e punteggio.
- _Organizzatore_: ha bisogno di tutte le valutazioni per proclamare il vincitore (caso d'uso rinviato).

**Pre-condizioni.**

- Il Giudice è autenticato ed è **assegnato** all'hackathon.
- L'hackathon è nello stato **"in valutazione"**.
- Esiste almeno una sottomissione da valutare.

**Post-condizioni (garanzia di successo).** La sottomissione selezionata ha una valutazione registrata, composta da **giudizio testuale** e **punteggio (0-10)**, attribuita dal Giudice.

**Scenario principale di successo.**

1. Il Giudice seleziona un hackathon, in stato "in valutazione", a cui è assegnato.
2. Il sistema mostra l'elenco delle sottomissioni dei team.
3. Il Giudice seleziona una sottomissione.
4. Il Giudice inserisce un giudizio scritto e un punteggio.
5. Il Giudice conferma la valutazione.
6. Il sistema verifica che il punteggio sia compreso tra 0 e 10.
7. Il sistema registra la valutazione e la associa alla sottomissione.
8. Il sistema conferma la registrazione della valutazione.

_Il Giudice ripete i passi 3-8 (ciclo **while**) finché restano sottomissioni da valutare._

**Estensioni (flussi alternativi).**

- **6a. Il punteggio è fuori dall'intervallo ammesso (< 0 o > 10):**
  - 6a.1 Il sistema segnala l'errore e torna al passo 4.
- **6b. Il giudizio scritto è vuoto:**
  - 6b.1 Il sistema segnala che il giudizio è obbligatorio e torna al passo 4.
- **1a. L'hackathon non è nello stato "in valutazione":**
  - 1a.1 Il sistema impedisce la valutazione e ne spiega il motivo.
- **3a. La sottomissione è già stata valutata:**
  - 3a.1 Il sistema mostra la valutazione esistente e ne consente la modifica _(da confermare — vedi Problemi aperti)_.

**Regole di business esercitate.**

- **Punteggio numerico compreso tra 0 e 10** (cfr. `progetto.MD` §Giudice) — verificata al passo 6.
- **Giudizio scritto obbligatorio**.
- La valutazione avviene quando l'hackathon è "in valutazione".

**Requisiti speciali / note implementative.** Validazione del range del punteggio lato server; definire se il punteggio è intero o decimale.

**Frequenza d'uso.** Concentrata nella fase "in valutazione".

**Problemi aperti.** Una sottomissione già valutata è **modificabile** o **bloccata**? Decidere e documentare.

**Suggerimento VP.** Il ciclo "ripete i passi 3-8" si modella con un operatore **while/loop** nel Flow of Events. Crea un Requirement "Punteggio ∈ [0,10]" e collegalo a UC05.

---

## 3. Concatenamento end-to-end (coerenza pre/post-condizioni)

La fetta verticale è coerente: la post-condizione di un caso d'uso abilita la pre-condizione del successivo.

```
UC01 Crea Hackathon     → hackathon "in iscrizione"
        │
UC02 Crea Team          → l'utente ha un team
        │
UC03 Iscrive Team       → team iscritto all'hackathon        (richiede hackathon "in iscrizione")
        │   (transizione automatica: "in iscrizione" → "in corso" alla scadenza iscrizioni)
UC04 Invia Sottomissione→ sottomissione registrata           (richiede hackathon "in corso")
        │   (transizione automatica: "in corso" → "in valutazione" alla data di fine)
UC05 Valuta Sottomissione → valutazione (giudizio + 0-10)     (richiede hackathon "in valutazione")
```

> In questa iterazione le transizioni di stato avvengono **automaticamente in base alle date** (vedi §1.4), quindi la fetta dei 5 casi d'uso è eseguibile end-to-end senza casi d'uso aggiuntivi. In iterazioni successive potranno diventare casi d'uso espliciti (es. `Avvia Hackathon`, `Inizia Fase Valutazione`) o essere gestite da un attore Tempo.

---

## 4. Appendice

### 4.1 Pre-condizione trasversale: Registrazione / Autenticazione

Ogni caso d'uso autenticato (UC01-UC05) presuppone che l'attore abbia effettuato la **Registrazione** (attore Visitatore) e l'**Autenticazione**. In Visual Paradigm puoi:

- modellarle come **pre-condizione** testuale (come fatto qui), oppure
- creare i casi d'uso `Autenticazione`/`Registrazione` e richiamarli nel _Flow of Events_ come **Referenced Use Case** (equivalente a `<<include>>`).

Esercitano lo **strato di sicurezza** dell'architettura (cfr. `ITERAZIONE_1_CASI_USO.md` §5 e `docs/05_ProgettazioneArchitetturale.pdf`).

> **Attori e autenticazione.** In `useCase.jpg` Visitatore, Utente e Membro del team sono **attori distinti, senza generalizzazione** tra loro. `Registrazione` e `Autenticazione` restano sul Visitatore (ingresso nel sistema); il requisito "essere autenticati" per UC01-UC05 è espresso come **pre-condizione** (o «include» `Autenticazione`), non tramite generalizzazione tra attori — cfr. `UML_02_CasiUso.pdf` slide 22. Le uniche generalizzazioni nel diagramma sono Organizzatore/Giudice/Mentore ▷ Membro staff e Amministratore Team ▷ Membro del team.

### 4.2 Contesto implementativo (vincoli di `progetto.MD`)

- Sviluppo in **Java**, poi portato su **Spring Boot**; presentazione anche solo CLI e/o **API REST**.
- Almeno **due design pattern** (cfr. `docs/PatternsGRASP.pdf`, `docs/Design Patterns.pptx.pdf`). Spunti coerenti con questi casi d'uso: _State_ per il ciclo di vita dell'hackathon (in iscrizione → in corso → in valutazione → concluso); _Repository/DAO_ per la persistenza; _Factory_ per la creazione delle entità di dominio.

### 4.3 Verso il modello di dominio

Gli attributi delle entità citate nei flussi andranno definiti nel **diagramma delle classi** (prossimo passo della progettazione). In prima approssimazione:

- **Hackathon**: nome, regolamento, scadenza iscrizioni, data inizio, data fine, luogo, premio, dimensione max team, stato (in iscrizione / in corso / in valutazione / concluso).
- **Team**: nome, membri, amministratore.
- **Sottomissione**: titolo, descrizione, collegamento/artefatti, data-ora ultimo aggiornamento.
- **Valutazione**: giudizio (testo), punteggio (0-10), giudice.

### 4.4 Riferimenti

- `progetto.MD` — regole di dominio e attori (fonte delle regole di business).
- `ITERAZIONE_1_CASI_USO.md` — scelta dei casi d'uso e fetta verticale.
- `REVISIONE_USE_CASE.md` — nomi finali di casi d'uso e attori (coerenti con `iterazione1/useCase/useCase.jpg`).
- `docs/UML_02_CasiUso.pdf` — formato dei casi d'uso (slide 11-15) ed esempio dettagliato (slide 26-30).
