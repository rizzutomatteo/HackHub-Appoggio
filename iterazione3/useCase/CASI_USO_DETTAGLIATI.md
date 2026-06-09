# Casi d'Uso Dettagliati — Terza Iterazione HackHub

## 1. Scopo e come usare questo documento con Visual Paradigm 17.3

Questo file contiene la **specifica in formato dettagliato** ("fully-dressed", stile Larman — cfr. esempio _Elabora Vendita_ in `docs/UML_02_CasiUso.pdf` slide 26-30) dei casi d'uso della **terza iterazione** di HackHub. Stessa struttura e convenzioni di `iterazione2/useCase/CASI_USO_DETTAGLIATI.md`.

La terza iterazione **attiva il Mentore** (attore primario finora con zero casi d'uso realizzati) e **introduce la prima integrazione esterna**, **Calendar**, per la pianificazione delle call. I casi d'uso formano la **catena del mentoring**:

| ID   | Caso d'uso                       | Attore primario     | Pattern in evidenza                       |
| ---- | -------------------------------- | ------------------- | ----------------------------------------- |
| UC11 | Richiede Supporto                | Membro del team     | Creator + **Observer** (notifica Mentori) |
| UC12 | Visualizza Richiesta di Supporto | Mentore             | Information Expert (scope via Incarico)   |
| UC13 | Propone Call                     | Mentore             | **Adapter** (Calendar) + Observer         |
| UC14 | Accetta Proposta Call            | Amministratore Team | **Adapter** (Calendar) + Observer         |
| UC15 | Rifiuta Proposta Call            | Amministratore Team | Observer (no Calendar)                    |

> Gli ID proseguono la numerazione (iter. 1 = UC01-UC05, iter. 2 = UC06-UC10). `Registrazione`/`Autenticazione` (attore Visitatore) restano **pre-condizione trasversale** non specificata in dettaglio (§4.1).

### 1.1 Mappatura sezione → campo di Visual Paradigm

In VP: seleziona il caso d'uso nel diagramma → tasto destro → **Open Use Case Details** (oppure doppio clic nella vista UeXceler). La descrizione breve sta invece in **Open Specification ▸ Documentation**.

| Sezione di questo documento                      | Dove inserirla in Visual Paradigm 17.3                     |
| ------------------------------------------------ | ---------------------------------------------------------- |
| Nome                                             | Nome del caso d'uso                                        |
| ID                                               | Prefisso nel nome (`UC11 …`) **oppure** un _Tagged Value_  |
| Portata (Scope)                                  | _Documentation_                                            |
| Livello                                          | _Use Case Details ▸ Details ▸ Level_                       |
| Rank / Complessità / Stato                       | _Basic Information ▸ Rank_; _Details ▸ Complexity/Status_  |
| Breve descrizione                                | _Open Specification ▸ Documentation_                       |
| Attore primario                                  | _Basic Information ▸ Primary Actors_                       |
| Attori secondari / di supporto                   | _Basic Information ▸ Supporting Actors_ (es. **Calendar**) |
| Parti interessate e interessi                    | _Documentation_ oppure _Details ▸ Assumptions_             |
| Pre-condizioni                                   | _Details ▸ Pre-conditions_                                 |
| Post-condizioni (garanzia di successo)           | _Details ▸ Post-conditions_                                |
| Scenario principale (flusso base)                | _Flow of Events ▸ Basic Flow_ (passi numerati)             |
| Estensioni / flussi alternativi                  | _Flow of Events ▸ Extensions_                              |
| Regole di business                               | _Requirements_ (collega un Requirement) o _Documentation_  |
| Requisiti speciali / Frequenza / Problemi aperti | _Details ▸ Assumptions_ / _Documentation_                  |

### 1.2 Strumenti di Visual Paradigm 17.3 da sfruttare

- **Flow of Events**: passi del flusso base uno per riga (VP li numera); ramificazioni con **if / while / jump**; per un UC incluso usa **"Referenced Use Case"**.
- **Attore secondario `Calendar`**: collega `Calendar` come **supporting actor** ai casi d'uso che lo usano (UC13 Propone Call, UC14 Accetta Proposta Call) — coerente con `useCase.jpg`. **UC15 Rifiuta NON è collegato a Calendar.**
- **Generazione automatica**: dal Flow of Events VP genera lo scheletro di **Sequence Diagram** — utile per UC13/UC14 (interazione con il gateway Calendar) e per l'Observer.
- **Requirements**: un Requirement per ogni regola di business (es. _"la prenotazione è delegata a Calendar"_, _"il Mentore vede solo i propri hackathon"_).
- **Test Plan**: un test per ogni estensione (in particolare i fallimenti di Calendar).

### 1.3 Convenzioni di scrittura (cfr. `docs/UML_02_CasiUso.pdf` slide 13-15)

- Passi **numerati**, in **forma attiva**: `N. Il <attore/sistema> <azione>` (mai forma passiva).
- Il **primo passo** è sempre un'azione dell'attore primario (attivazione).
- Ramificazioni con la parola **"Se"** e indici annidati (`x.1`, `x.2`).
- **Estensioni** etichettate con il numero del passo + lettera (`3a`, `4a`); `*a` = in qualunque momento; un'estensione non dovrebbe avere a sua volta estensioni.
- **Ordine delle estensioni d'errore**: prima la condizione negativa/di errore (guard breve + rifiuto), poi l'happy path; coerente con la convenzione `alt`/`opt` dei diagrammi di sequenza in `CLAUDE.md`.

### 1.4 Selezione dei casi d'uso di questa iterazione

La selezione segue la metodologia del **Processo Unificato** (iterativo/incrementale, **use-case driven**, **architecture-centric**, **risk-driven**; criteri Larman: copertura architetturale, valore, riduzione del rischio) e l'analisi "council" della scelta iter. 3.

Dopo iter. 1 (scheletro del valore) e iter. 2 (ciclo di vita + team reali con State/Observer), restano due gap che questa iterazione affronta:

1. **Il Mentore è l'unico attore primario con 0 casi d'uso realizzati.** `progetto.MD` lo descrive ricco: "_visualizza le richieste di supporto inviate dai team e può proporre una call, la cui prenotazione è gestita tramite un sistema calendar esterno_". UC11-UC15 ne realizzano la **catena del mentoring**.
2. **Nessuna integrazione esterna è mai stata esercitata** (rischio tecnico residuo, UP risk-driven). Questa iterazione ritira l'integrazione **Calendar** e introduce un nuovo concern architetturale: il **gateway verso un sistema esterno** (pattern **Adapter** — terza famiglia di pattern, dopo State e Observer comportamentali).

**Rinviato a iter. 4:** Paga Montepremi (+Sistema Pagamento), moderazione (Segnala Team / Gestisci Segnalazioni), gestione team/staff e viste read-only. **Debito noto:** Autenticazione reale (oggi stub) — l'unico strato architetturale mai esercitato.

### 1.5 Integrazione con Calendar (contratto fissato)

`progetto.MD` (§Calendar): "_servizio esterno utilizzato esclusivamente per pianificare le call tra mentore e team. La piattaforma vi **delega** la prenotazione degli slot._" Quindi:

- **Isolamento**: l'accesso a Calendar passa **solo** da un **Adapter** `CalendarGateway` (pattern Adapter); i `service` non chiamano direttamente l'API esterna.
- **Contratto sincrono**: la piattaforma invoca Calendar e attende l'esito (conferma / errore). Gestione degli errori esplicita nelle estensioni (Calendar non disponibile, slot non disponibile).
- **Chi tocca Calendar:**

| Caso d'uso                 | Interazione con Calendar                                                           |
| -------------------------- | ---------------------------------------------------------------------------------- |
| UC13 Propone Call          | **verifica/riserva** lo slot (prenotazione _tentativa_)                            |
| UC14 Accetta Proposta Call | **conferma** la prenotazione dello slot                                            |
| UC15 Rifiuta Proposta Call | **nessuna** (coerente con `useCase.jpg`; lo slot tentativo viene rilasciato/scade) |

### 1.6 Decisioni di progettazione fissate (analisi "council")

- **D1 — Contratto Calendar.** Sincrono; UC13 (riserva tentativa) e UC14 (conferma) interagiscono con Calendar tramite `CalendarGateway`; UC15 no. Fallimenti di Calendar gestiti come estensioni (non si lascia il comportamento implicito).
- **D2 — Stato di `RichiestaSupporto` e `PropostaCall` = enum semplice**, **NON** un secondo pattern State (il GoF State resta riservato all'Hackathon — coerente con `Invito.stato` di iter. 2, per non diluire la narrativa dei pattern).
- **D3 — Scope del Mentore via `Incarico`.** Il Mentore vede/agisce **solo** sugli hackathon a cui è assegnato (Incarico, ruolo = Mentore) — `progetto.MD` §Membro dello Staff: "_solo per gli hackathon cui è assegnato come staff_". Stessa convenzione stub di auth degli altri UC (utente corrente = parametro).
- **D4 — Nuove entità + Adapter.** Si introducono `RichiestaSupporto` e `PropostaCall` (con enum di stato); l'integrazione esterna è incapsulata da `CalendarGateway` (**Adapter**); l'**Observer** di iter. 2 è **riusato** per le notifiche (richiesta → Mentori; proposta/esito → controparte).

---

## 2. Specifiche dei casi d'uso

### UC11 — Richiede Supporto

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Richiede Supporto |
| **ID**                 | UC11              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Membro del team   |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Media / Bassa     |

**Breve descrizione.** Un Membro del team invia una **richiesta di supporto** ai Mentori dell'hackathon a cui il suo team è iscritto. Apre la catena del mentoring; i Mentori dell'hackathon vengono notificati (Observer).

**Evento scatenante.** Il team ha bisogno di aiuto durante lo svolgimento dell'hackathon.

**Parti interessate e interessi.**

- _Membro / team_: vuole ricevere supporto da un Mentore.
- _Mentori dell'hackathon_: vogliono vedere le richieste per affiancare i team.

**Pre-condizioni.**

- L'attore è autenticato ed è **membro di un team iscritto** all'hackathon. _(rimando: Autenticazione, §4.1)_
- L'hackathon è nello stato **"in corso"** (il supporto avviene durante l'evento).

**Post-condizioni (garanzia di successo).**

- Esiste una **`RichiestaSupporto`** in stato **"Aperta"** associata a (team, hackathon).
- I **Mentori** dell'hackathon sono stati **notificati** (Observer).
- _Oppure_ l'operazione è annullata e nessuna richiesta viene creata.

**Scenario principale di successo.**

1. Il Membro del team richiede di inviare una richiesta di supporto per l'hackathon a cui il team è iscritto.
2. Il sistema presenta il modulo (messaggio della richiesta).
3. Il Membro inserisce il messaggio e conferma.
4. Il sistema crea la `RichiestaSupporto` (stato "Aperta") associata a (team, hackathon).
5. Il sistema notifica i Mentori dell'hackathon (Observer).
6. Il sistema conferma l'invio.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **1a. Il team non è iscritto ad alcun hackathon "in corso":**
  - 1a.1 Il sistema impedisce la richiesta e ne spiega il motivo.
  - 1a.2 exit Scenario.
- **3a. (opzionale) Esiste già una richiesta "Aperta" del team per quell'hackathon:**
  - 3a.1 Il sistema avvisa ed evita il duplicato (riusa quella esistente).
  - 3a.2 jump to 2.
- **\*a. In qualunque momento il Membro annulla:** nessuna richiesta creata.

**Regole di business esercitate.**

- Il supporto si richiede mentre l'hackathon è **"in corso"**.
- La richiesta è visibile ai **Mentori di quell'hackathon** (Incarico, ruolo = Mentore) — vedi UC12.

**Requisiti speciali / note implementative.** Nuova entità `RichiestaSupporto` con `stato` = **enum** `StatoRichiesta` (NON pattern State, vedi §4.3). **Observer** riusato per notificare i Mentori. Auth non realizzata: il membro corrente entra come parametro-stub.

**Frequenza d'uso.** Media (durante la fase "in corso").

**Problemi aperti.** Destinatario: tutti i Mentori dell'hackathon o il team ne sceglie uno? Stato della richiesta: ciclo `Aperta → Gestita → Chiusa` e chi la chiude. Quando il supporto è ammesso (solo "in corso" o anche prima?).

**Suggerimento VP.** Genera un Sequence Diagram (Membro → Handler → Service → `RichiestaSupporto`; poi `ServizioNotifiche` → Mentori). Crea Requirement "Richiesta visibile ai Mentori dell'hackathon".

---

### UC12 — Visualizza Richiesta di Supporto

| Campo                  | Valore                           |
| ---------------------- | -------------------------------- |
| **Nome**               | Visualizza Richiesta di Supporto |
| **ID**                 | UC12                             |
| **Portata**            | HackHub (sistema)                |
| **Livello**            | Obiettivo utente                 |
| **Attore primario**    | Mentore                          |
| **Attori di supporto** | nessuno                          |
| **Rank / Complessità** | Bassa / Bassa                    |

**Breve descrizione.** Il Mentore visualizza le **richieste di supporto** dei team relative agli hackathon a cui è **assegnato** come staff. È l'ingresso operativo del Mentore (tipicamente dopo la notifica di UC11) e precede _Propone Call_ (UC13).

**Evento scatenante.** Il Mentore vuole affiancare i team / ha ricevuto la notifica di una nuova richiesta.

**Parti interessate e interessi.**

- _Mentore_: vuole vedere chi ha bisogno di supporto, solo per i propri hackathon.
- _Team richiedenti_: vogliono essere visti e presi in carico.

**Pre-condizioni.**

- L'attore è autenticato ed è **Mentore assegnato** (Incarico, ruolo = Mentore) ad almeno un hackathon. _(rimando: Autenticazione, §4.1)_

**Post-condizioni (garanzia di successo).**

- Il sistema mostra le richieste di supporto degli hackathon del Mentore. _(operazione di sola lettura: nessun cambiamento di stato.)_

**Scenario principale di successo.**

1. Il Mentore richiede l'elenco delle richieste di supporto.
2. Il sistema recupera le `RichiestaSupporto` degli hackathon a cui il Mentore è assegnato (Incarico).
3. Il sistema mostra l'elenco (team, hackathon, messaggio, stato).
4. _Il Mentore può selezionare una richiesta per vederne i dettagli._

**Estensioni (flussi alternativi).**

- **1a. Il Mentore non è assegnato ad alcun hackathon:**
  - 1a.1 Il sistema non mostra alcuna richiesta (nessun hackathon di competenza).
- **2a. Nessuna richiesta presente** per gli hackathon del Mentore:
  - 2a.1 Il sistema mostra un elenco vuoto.

**Regole di business esercitate.**

- Il Mentore vede **solo** le richieste degli hackathon a cui è **assegnato come staff** (`progetto.MD` §Membro dello Staff) — filtro via `Incarico`.

**Requisiti speciali / note implementative.** Sola lettura. Scope tramite `Incarico` (ruolo = Mentore) — stessa convenzione stub di auth (§4.1). Information Expert: il sistema sa quali hackathon competono al Mentore.

**Frequenza d'uso.** Media.

**Problemi aperti.** Filtro per stato (solo "Aperta" vs tutte) e ordinamento (per data). Eventuale paginazione.

**Suggerimento VP.** Collega questo UC come passo che precede UC13 (il Mentore seleziona una richiesta e propone una call). Requirement "Mentore: visibilità limitata ai propri hackathon".

---

### UC13 — Propone Call

| Campo                  | Valore                         |
| ---------------------- | ------------------------------ |
| **Nome**               | Propone Call                   |
| **ID**                 | UC13                           |
| **Portata**            | HackHub (sistema)              |
| **Livello**            | Obiettivo utente               |
| **Attore primario**    | Mentore                        |
| **Attori di supporto** | **Calendar** (sistema esterno) |
| **Rank / Complessità** | Alta / Media                   |

**Breve descrizione.** Il Mentore propone una **call** a un team (tipicamente in risposta a una richiesta di supporto), indicando uno **slot**; la **prenotazione dello slot è delegata a Calendar** (riserva tentativa). Il team viene notificato (Observer).

**Evento scatenante.** Il Mentore vuole pianificare una call con un team che ha chiesto supporto.

**Parti interessate e interessi.**

- _Mentore_: vuole concordare un orario per la call.
- _Team_: vuole ricevere la proposta e poterla accettare/rifiutare.
- _Calendar_: gestisce gli slot di prenotazione.

**Pre-condizioni.**

- L'attore è autenticato ed è **Mentore assegnato** all'hackathon del team. _(rimando: Autenticazione, §4.1)_
- Esiste il **team destinatario**, iscritto a quell'hackathon (tipicamente con una `RichiestaSupporto` aperta).

**Post-condizioni (garanzia di successo).**

- Esiste una **`PropostaCall`** in stato **"Proposta"** verso il team, con lo **slot riservato (tentativo) su Calendar**.
- Il **team** è stato **notificato** (Observer).
- _Oppure_ l'operazione è annullata / fallita e nessuna proposta viene creata.

**Scenario principale di successo.**

1. Il Mentore seleziona una richiesta di supporto (o un team) e richiede di proporre una call.
2. Il sistema presenta il modulo (data/ora dello slot proposto).
3. Il Mentore inserisce lo slot e conferma.
4. Il sistema, tramite `CalendarGateway`, **verifica e riserva** lo slot su **Calendar** (sincrono).
5. Il sistema crea la `PropostaCall` (stato "Proposta") verso il team, collegata alla richiesta di supporto.
6. Il sistema notifica il team (Observer).
7. Il sistema conferma l'invio della proposta.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **1a. Il Mentore non è assegnato all'hackathon del team:**
  - 1a.1 Il sistema rifiuta (fuori competenza — Incarico).
  - 1a.2 exit Scenario.
- **4a. Calendar non disponibile** (errore del sistema esterno):
  - 4a.1 Il sistema segnala che la proposta non può essere creata ora e invita a riprovare.
  - 4a.2 exit Scenario (nessuna `PropostaCall` creata).
- **4b. Slot non disponibile** (già occupato su Calendar):
  - 4b.1 Il sistema segnala il conflitto.
  - 4b.2 jump to 2 (il Mentore sceglie un altro slot).
- **\*a. In qualunque momento il Mentore annulla:** nessuna proposta; eventuale slot tentativo rilasciato.

**Regole di business esercitate.**

- La **prenotazione dello slot è delegata a Calendar** (`progetto.MD` §Mentore/§Calendar).
- Il Mentore propone call **solo** per i team degli hackathon a cui è assegnato (Incarico).

**Requisiti speciali / note implementative.** Nuova entità `PropostaCall` con `stato` = **enum** `StatoCall` (NON pattern State). **Adapter** `CalendarGateway` isola l'API esterna (contratto sincrono, §1.5). **Observer** notifica il team. Auth: Mentore corrente come parametro-stub.

**Frequenza d'uso.** Media.

**Problemi aperti.** Slot tentativo (alla proposta) vs definitivo (all'accettazione, UC14): meccanismo di rilascio/scadenza del tentativo se la proposta viene rifiutata/ignorata. Più proposte per la stessa richiesta. Scadenza della proposta.

**Suggerimento VP.** Sequence Diagram con la linea di vita `:CalendarGateway` (Adapter) verso `Calendar`; mostra la riserva sincrona e la notifica Observer al team. Requirement "Prenotazione slot delegata a Calendar".

---

### UC14 — Accetta Proposta Call

| Campo                  | Valore                         |
| ---------------------- | ------------------------------ |
| **Nome**               | Accetta Proposta Call          |
| **ID**                 | UC14                           |
| **Portata**            | HackHub (sistema)              |
| **Livello**            | Obiettivo utente               |
| **Attore primario**    | Amministratore Team            |
| **Attori di supporto** | **Calendar** (sistema esterno) |
| **Rank / Complessità** | Media / Media                  |

**Breve descrizione.** L'Amministratore del team **accetta** una proposta di call; il sistema **conferma la prenotazione dello slot su Calendar**; la call risulta prenotata e il Mentore è notificato (Observer).

**Evento scatenante.** Il team ha ricevuto una proposta di call (UC13) e la vuole confermare.

**Parti interessate e interessi.**

- _Amministratore / team_: vuole fissare la call con il Mentore.
- _Mentore_: vuole sapere se la proposta è accettata.
- _Calendar_: registra la prenotazione confermata.

**Pre-condizioni.**

- L'attore è autenticato ed è **Amministratore del team** destinatario. _(rimando: Autenticazione, §4.1)_
- Esiste una **`PropostaCall`** in stato **"Proposta"** verso il team.

**Post-condizioni (garanzia di successo).**

- La `PropostaCall` è in stato **"Accettata"** e lo **slot è prenotato su Calendar** (con riferimento di prenotazione).
- Il **Mentore** è stato **notificato** (Observer).
- _Oppure_ la prenotazione fallisce / la proposta non è più valida e nulla cambia.

**Scenario principale di successo.**

1. L'Amministratore consulta le proposte di call ricevute dal team.
2. L'Amministratore seleziona una proposta e la accetta.
3. Il sistema verifica che la proposta sia ancora in stato "Proposta".
4. Il sistema, tramite `CalendarGateway`, **conferma la prenotazione** dello slot su **Calendar** (sincrono).
5. Il sistema pone la `PropostaCall` in stato "Accettata" e registra il riferimento di prenotazione.
6. Il sistema notifica il Mentore (Observer).
7. Il sistema conferma l'accettazione.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **3a. La proposta non è più valida** (già accettata/rifiutata o ritirata):
  - 3a.1 Il sistema informa che la proposta non è più disponibile.
  - 3a.2 exit Scenario.
- **4a. Calendar non disponibile o slot non più disponibile** (es. scaduto/occupato nel frattempo):
  - 4a.1 Il sistema segnala il fallimento della prenotazione; la proposta resta "Proposta" (su scadenza/ritiro della proposta vedi Problemi aperti, §4.5).
  - 4a.2 exit Scenario.
- **\*a. In qualunque momento l'Amministratore annulla:** nessuna prenotazione.

**Regole di business esercitate.**

- L'accettazione **prenota** lo slot su Calendar (delega).
- Solo l'**Amministratore del team** può accettare (coerente con `useCase.jpg`).
- **Idempotenza**: una proposta già accettata non viene ri-prenotata (3a).

**Requisiti speciali / note implementative.** **Adapter** `CalendarGateway` (conferma prenotazione, contratto sincrono). **Observer** notifica il Mentore. Opera sull'entità `PropostaCall` (UC13).

**Frequenza d'uso.** Media.

**Problemi aperti.** Chi può accettare (solo Amministratore — da diagramma — o qualunque membro?). Effetto sulle altre proposte pendenti per lo stesso team. Gestione fuso orario dello slot.

**Suggerimento VP.** Sequence Diagram: ordina gli `alt` con gli errori (3a/4a) **prima** dell'happy path; mostra `:CalendarGateway` → `Calendar` (conferma) e l'Observer verso il Mentore. Requirement "Accettazione = prenotazione confermata su Calendar".

---

### UC15 — Rifiuta Proposta Call

| Campo                  | Valore                |
| ---------------------- | --------------------- |
| **Nome**               | Rifiuta Proposta Call |
| **ID**                 | UC15                  |
| **Portata**            | HackHub (sistema)     |
| **Livello**            | Obiettivo utente      |
| **Attore primario**    | Amministratore Team   |
| **Attori di supporto** | nessuno               |
| **Rank / Complessità** | Bassa / Bassa         |

**Breve descrizione.** L'Amministratore del team **rifiuta** una proposta di call; la proposta passa a "Rifiutata"; **nessuna prenotazione** viene confermata su Calendar (lo slot tentativo viene rilasciato/scade). Il Mentore è notificato (Observer).

**Evento scatenante.** Il team non può/non vuole svolgere la call proposta.

**Parti interessate e interessi.**

- _Amministratore / team_: vuole declinare la proposta.
- _Mentore_: vuole sapere che la proposta è stata rifiutata (per riproporre).

**Pre-condizioni.**

- L'attore è autenticato ed è **Amministratore del team** destinatario. _(rimando: Autenticazione, §4.1)_
- Esiste una **`PropostaCall`** in stato **"Proposta"** verso il team.

**Post-condizioni (garanzia di successo).**

- La `PropostaCall` è in stato **"Rifiutata"**; **nessuna prenotazione confermata**.
- Il **Mentore** è stato **notificato** (Observer).

**Scenario principale di successo.**

1. L'Amministratore consulta le proposte di call ricevute dal team.
2. L'Amministratore seleziona una proposta e la rifiuta.
3. Il sistema verifica che la proposta sia ancora in stato "Proposta".
4. Il sistema pone la `PropostaCall` in stato "Rifiutata".
5. Il sistema notifica il Mentore (Observer).
6. Il sistema conferma il rifiuto.

**Estensioni (flussi alternativi).**

- **3a. La proposta non è più valida** (già accettata/rifiutata o ritirata):
  - 3a.1 Il sistema informa che la proposta non è più disponibile.
  - 3a.2 exit Scenario.
- **\*a. In qualunque momento l'Amministratore annulla:** nessun cambiamento.

**Regole di business esercitate.**

- Il rifiuto **non** prenota nulla su Calendar; lo slot tentativo riservato in UC13 **viene rilasciato/scade** (vedi Problemi aperti).
- Solo l'**Amministratore del team** può rifiutare.

**Requisiti speciali / note implementative.** **Non** interagisce con Calendar (coerente con `useCase.jpg`: `Rifiuta Proposta Call` non è collegato a `Calendar`). **Observer** notifica il Mentore. Opera sull'entità `PropostaCall`.

**Frequenza d'uso.** Media.

**Problemi aperti.** **Rilascio dello slot tentativo su Calendar**: se richiede una chiamata esplicita, allora UC15 toccherebbe Calendar (ma il diagramma **non** lo collega) → scelta attuale: lo slot tentativo **scade da solo** su Calendar. Decidere e documentare.

**Suggerimento VP.** Sequence Diagram senza linea di vita Calendar (solo cambio stato + Observer verso il Mentore). Requirement "Il rifiuto non prenota su Calendar".

---

## 3. Concatenamento end-to-end (coerenza pre/post-condizioni e dipendenze)

La catena del mentoring è un flusso secondario completo: la post-condizione di un caso d'uso abilita la pre-condizione del successivo.

```
UC11 Richiede Supporto      → RichiestaSupporto "Aperta" + notifica Mentori   (hackathon "in corso")
        │  (Observer)
UC12 Visualizza Richiesta   → il Mentore vede le richieste dei propri hackathon   (scope: Incarico)
        │
UC13 Propone Call           → PropostaCall "Proposta" + slot riservato su Calendar + notifica team
        │  (Adapter Calendar, Observer)
        ├─ UC14 Accetta Proposta Call → "Accettata" + prenotazione confermata su Calendar + notifica Mentore
        └─ UC15 Rifiuta Proposta Call → "Rifiutata" (no Calendar) + notifica Mentore
```

**Dipendenze chiave.** UC13 presuppone tipicamente una `RichiestaSupporto` (UC11) ed è eseguito dal Mentore dopo UC12. UC14/UC15 presuppongono una `PropostaCall` "Proposta" (UC13). Il **Mentore** (attivato qui per la prima volta) e l'**Amministratore Team** (introdotto in iter. 2) collaborano via Calendar.

**Continuità coi pattern.** Lo **State** resta sul solo Hackathon (iter. 1-2). L'**Observer** (iter. 2) è **riusato** per tutte le notifiche del mentoring. Si aggiunge l'**Adapter** (Calendar) come terza famiglia di pattern.

---

## 4. Appendice

### 4.1 Pre-condizione trasversale: Registrazione / Autenticazione

Come nelle iterazioni precedenti, ogni caso d'uso autenticato presuppone **Registrazione** (attore Visitatore) e **Autenticazione**; restano **pre-condizione** (o «include» `Autenticazione`, cfr. `docs/UML_02_CasiUso.pdf` slide 22). L'**utente corrente** (Membro in UC11, Mentore in UC12-UC13, Amministratore in UC14-UC15) entra come **parametro-stub**. In particolare lo **scope del Mentore** (vedere/agire solo sui propri hackathon, UC12-UC13) si appoggia all'`Incarico` (ruolo = Mentore), non a un'autenticazione reale: l'auth resta il debito noto da affrontare in un'iterazione dedicata.

### 4.2 Contesto implementativo (vincoli di `progetto.MD`)

- Sviluppo in **Java**, poi **Spring Boot**; presentazione anche solo CLI e/o **API REST**.
- **Almeno due design pattern**: già soddisfatto (State + Observer). Questa iterazione aggiunge l'**Adapter** (gateway Calendar) — una **terza famiglia** (strutturale), giustificata dal requisito di delega a un sistema esterno (non gold-plating).

### 4.3 Impatti sul modello di dominio e di progettazione (delta da portare nei file `*CHANGE`)

Da portare nei diagrammi di analisi/progettazione di iter. 3 come file `*CHANGE` (non toccare i `*REALIZZATI`):

- **`RichiestaSupporto`** (nuova entità): `messaggio`, `dataRichiesta`, `stato : StatoRichiesta`. Associazioni: `Team` (richiedente) e `Hackathon` (contesto); visibile ai Mentori di quell'hackathon (via `Incarico`).
- **`PropostaCall`** (nuova entità): `dataOraProposta`, `stato : StatoCall`, `riferimentoPrenotazione` (id slot Calendar). Associazioni: `Mentore` (proponente, via Incarico), `Team` (destinatario), e (0..1) `RichiestaSupporto` collegata.
- **Enum**: `StatoRichiesta { Aperta, Gestita, Chiusa }`, `StatoCall { Proposta, Accettata, Rifiutata }` — **enum semplici, NON pattern State**.
- **`CalendarGateway`** (**Adapter**, dominio della soluzione): interfaccia/porta della piattaforma (`verificaDisponibilità`, `riservaSlot`, `confermaPrenotazione`) + adapter concreto verso l'API esterna `Calendar`. I `service` usano solo la porta.
- **Observer** (riuso di iter. 2): `ServizioNotifiche` (Subject) notifica i `Mentore`/`Utente` (Observer) per richieste e proposte/esiti.
- **EBC/persistenza** coerenti con la convenzione (vedi `CONVENZIONI_DIAGRAMMI.md`): `Handler*` per-UC (`HandlerRichiedeSupporto`, `HandlerVisualizzaRichieste`, `HandlerProponeCall`, `HandlerAccettaCall`, `HandlerRifiutaCall`), `service` (es. `GestoreSupporto`, `GestoreCall`), `Repo*` (`RepoRichiestaSupporto`, `RepoPropostaCall`).

### 4.4 Pattern per caso d'uso

| UC   | Pattern in evidenza                          | Note                                                          |
| ---- | -------------------------------------------- | ------------------------------------------------------------- |
| UC11 | **Observer** (notifica), Creator, Controller | crea `RichiestaSupporto`; notifica i Mentori; niente Calendar |
| UC12 | Information Expert, Controller               | scope via `Incarico`; sola lettura                            |
| UC13 | **Adapter** (Calendar), Observer, Creator    | riserva slot su Calendar; crea `PropostaCall`; notifica team  |
| UC14 | **Adapter** (Calendar), Observer             | conferma prenotazione su Calendar; notifica Mentore           |
| UC15 | **Observer**, Controller                     | cambio stato; **niente** Calendar; notifica Mentore           |

> **Copertura pattern iter. 3:** si **riusa** Observer e si **introduce** l'**Adapter** (Calendar). Lo **State** non è coinvolto (le entità di iter. 3 usano enum di stato — D2).

### 4.5 Problemi aperti consolidati (decisioni da confermare)

1. **Rilascio slot tentativo** al rifiuto/scadenza (UC13/UC15): scadenza automatica su Calendar (scelta attuale) vs chiamata esplicita di rilascio (che farebbe toccare Calendar a UC15, contro il diagramma).
2. **Ciclo di vita `RichiestaSupporto`** (`Aperta → Gestita → Chiusa`): chi e quando la chiude (alla call svolta? manualmente dal Mentore?).
3. **Destinatario della richiesta** (UC11): tutti i Mentori dell'hackathon o uno scelto dal team.
4. **Calendar sincrono vs asincrono**: qui **sincrono** (§1.5); valutare async se il sistema esterno lo richiede.
5. **Auth reale**: ancora stub; lo scope del Mentore si regge su `Incarico`.

### 4.6 Riferimenti

- `progetto.MD` — §Mentore (visualizza richieste, propone call con prenotazione delegata a Calendar, segnala violazioni), §Calendar (esterno, delega prenotazione slot), §Membro dello Staff (accesso solo agli hackathon assegnati).
- `docs/02_Processo.pdf` — UP iterativo/incrementale, risk-driven, architecture-centric.
- `docs/UML_02_CasiUso.pdf` — formato dei casi d'uso (slide 11-15), esempio dettagliato (26-30), attori secondari/«include» (22).
- `docs/Design Patterns.pptx.pdf` — **Adapter** (strutturale, integrazione con sistemi esterni) e **Observer** (notifiche).
- `docs/PatternsGRASP.pdf` — Controller, Creator, Information Expert, Pure Fabrication.
- `iterazione2/useCase/CASI_USO_DETTAGLIATI.md` — formato di riferimento e convenzioni; `CONVENZIONI_DIAGRAMMI.md` — convenzioni dei diagrammi.
- `iterazione1/useCase/useCase.puml` — modello dei casi d'uso (attori Mentore/Amministratore Team/Calendar e relazioni con Calendar).
