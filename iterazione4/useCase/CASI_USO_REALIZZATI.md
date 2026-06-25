# Casi d'Uso Dettagliati — Quarta Iterazione HackHub (ultima)

## 1. Scopo e come usare questo documento con Visual Paradigm 17.3

Questo file contiene la **specifica in formato dettagliato** ("fully-dressed", stile Larman — cfr. esempio _Elabora Vendita_ in `docs/UML_02_CasiUso.pdf` slide 26-30) dei casi d'uso della **quarta e ultima iterazione** di HackHub. Stessa struttura e convenzioni di `iterazione3/useCase/CASI_USO_DETTAGLIATI_REALIZZATI.md`.

La quarta iterazione è quella di **chiusura**: completa la **catena del valore** (erogazione del premio al vincitore), introduce la **governance** (moderazione: segnalazioni dei Mentori e decisioni dell'Organizzatore), e salda l'**accesso** (attiva l'attore **Visitatore**, finora privo di casi d'uso). I casi d'uso coprono i tre temi residui di `progetto.MD`:

| ID   | Caso d'uso                      | Attore primario | Tema       | Pattern in evidenza                             |
| ---- | ------------------------------- | --------------- | ---------- | ----------------------------------------------- |
| UC16 | Paga Montepremi                 | Organizzatore   | Valore     | **Adapter** (Sistema di Pagamento) [+ Observer] |
| UC17 | Segnala Team                    | Mentore         | Governance | **Observer** (notifica Organizzatore)           |
| UC18 | Gestisci Segnalazione           | Organizzatore   | Governance | Information Expert (scope via Incarico)         |
| UC19 | Registrazione                   | Visitatore      | Accesso    | Creator (crea Utente) + Controller              |
| UC20 | Autenticazione                  | Visitatore      | Accesso    | Controller (verifica credenziali)               |
| UC21 | Accedi a informazioni pubbliche | Visitatore      | Accesso    | Information Expert (sola lettura)               |
| UC22 | Aggiungi Mentore                | Organizzatore   | Staff      | Creator (Incarico) — riuso `assegnaStaff`       |

> Gli ID proseguono la numerazione (iter. 1 = UC01-UC05, iter. 2 = UC06-UC10, iter. 3 = UC11-UC15). Con questa iterazione **ogni attore di `progetto.MD` ha almeno un caso d'uso** e **ogni capacità esplicita della traccia risulta coperta** (verifica puntuale nella **matrice di copertura, §3.1**); inoltre **entrambi i sistemi esterni** (Calendar in iter. 3, Sistema di Pagamento qui) sono integrati con lo **stesso pattern Adapter**.

### 1.1 Mappatura sezione → campo di Visual Paradigm 17.3

In VP: seleziona il caso d'uso nel diagramma → tasto destro → **Open Use Case Details** (oppure doppio clic nella vista UeXceler). La descrizione breve sta invece in **Open Specification ▸ Documentation**.

| Sezione di questo documento                      | Dove inserirla in Visual Paradigm 17.3                                 |
| ------------------------------------------------ | ---------------------------------------------------------------------- |
| Nome                                             | Nome del caso d'uso                                                    |
| ID                                               | Prefisso nel nome (`UC16 …`) **oppure** un _Tagged Value_              |
| Portata (Scope)                                  | _Documentation_                                                        |
| Livello                                          | _Use Case Details ▸ Details ▸ Level_                                   |
| Rank / Complessità / Stato                       | _Basic Information ▸ Rank_; _Details ▸ Complexity/Status_              |
| Breve descrizione                                | _Open Specification ▸ Documentation_                                   |
| Attore primario                                  | _Basic Information ▸ Primary Actors_                                   |
| Attori secondari / di supporto                   | _Basic Information ▸ Supporting Actors_ (es. **Sistema di Pagamento**) |
| Parti interessate e interessi                    | _Documentation_ oppure _Details ▸ Assumptions_                         |
| Pre-condizioni                                   | _Details ▸ Pre-conditions_                                             |
| Post-condizioni (garanzia di successo)           | _Details ▸ Post-conditions_                                            |
| Scenario principale (flusso base)                | _Flow of Events ▸ Basic Flow_ (passi numerati)                         |
| Estensioni / flussi alternativi                  | _Flow of Events ▸ Extensions_                                          |
| Regole di business                               | _Requirements_ (collega un Requirement) o _Documentation_              |
| Requisiti speciali / Frequenza / Problemi aperti | _Details ▸ Assumptions_ / _Documentation_                              |

### 1.2 Strumenti di Visual Paradigm 17.3 da sfruttare

- **Flow of Events**: passi del flusso base uno per riga (VP li numera); ramificazioni con **if / while / jump**; per un UC incluso usa **"Referenced Use Case"**.
- **Attore secondario `Sistema di Pagamento`**: collegalo come **supporting actor** all'unico caso d'uso che lo usa (UC16 Paga Montepremi) — simmetrico a come `Calendar` è collegato a UC13/UC14 in iter. 3.
- **Generazione automatica**: dal Flow of Events VP genera lo scheletro di **Sequence Diagram** — utile per UC16 (interazione con il gateway di pagamento) e per l'Observer di UC17.
- **Requirements**: un Requirement per ogni regola di business (es. _"il premio è erogato solo a hackathon concluso con vincitore"_, _"il Mentore segnala solo i team dei propri hackathon"_, _"il Visitatore consulta solo informazioni pubbliche"_).
- **Test Plan**: un test per ogni estensione (in particolare i fallimenti del Sistema di Pagamento e i duplicati di pagamento).

### 1.3 Convenzioni di scrittura (cfr. `docs/UML_02_CasiUso.pdf` slide 13-15)

- Passi **numerati**, in **forma attiva**: `N. Il <attore/sistema> <azione>` (mai forma passiva).
- Il **primo passo** è sempre un'azione dell'attore primario (attivazione).
- Ramificazioni con la parola **"Se"** e indici annidati (`x.1`, `x.2`).
- **Estensioni** etichettate con il numero del passo + lettera (`3a`, `4a`); `*a` = in qualunque momento; un'estensione non dovrebbe avere a sua volta estensioni.
- **Ordine delle estensioni d'errore**: prima la condizione negativa/di errore (guard breve + rifiuto), poi l'happy path; coerente con la convenzione `alt`/`opt` dei diagrammi di sequenza in `CLAUDE.md`.

### 1.4 Selezione dei casi d'uso di questa iterazione (analisi "council")

La selezione segue la metodologia del **Processo Unificato** (iterativo/incrementale, **use-case driven**, **architecture-centric**, **risk-driven**; criteri Larman: copertura architetturale, valore, riduzione del rischio) e l'analisi "council" eseguita a fine iter. 3. Essendo l'**ultima** iterazione, il criterio dominante è la **completezza rispetto a `progetto.MD`**: nessun requisito esplicito e nessun attore devono restare non indirizzati.

Dopo iter. 1 (scheletro del valore), iter. 2 (ciclo di vita + team reali, State/Observer) e iter. 3 (mentoring + Calendar, Adapter), restano **quattro gap** tutti ancorati al testo della traccia:

1. **La catena del valore non si chiude.** `progetto.MD` (§Organizzatore + §Sistema di Pagamento) prevede l'erogazione del **premio in denaro al team vincitore**; finora UC08 proclama il vincitore ma il premio non viene mai pagato. **UC16 Paga Montepremi** chiude la catena e ritira il **secondo (e ultimo) sistema esterno**, riusando l'**Adapter** già introdotto per Calendar.
2. **La governance manca del tutto.** `progetto.MD` (§Mentore) dà al Mentore il dovere di "_segnalare il team all'Organizzatore per le decisioni del caso_"; oggi non esiste. **UC17 Segnala Team** + **UC18 Gestisci Segnalazione** realizzano il ciclo disciplinare, riusando l'**Observer**.
3. **L'attore Visitatore non ha alcun caso d'uso.** `progetto.MD` (§Visitatore) lo descrive: "_accede liberamente al sito per consultare le informazioni pubbliche sugli hackathon. Non può accedere ad altre funzionalità finché non effettua la registrazione e l'accesso._" **UC19 Registrazione**, **UC20 Autenticazione** e **UC21 Accedi a informazioni pubbliche** lo collegano al modello e saldano il **debito noto dell'autenticazione** (finora stub).
4. **Un requisito esplicito non modellato.** `progetto.MD` (§Organizzatore) dice che l'Organizzatore "_può aggiungere più Mentori all'hackathon anche successivamente alla sua creazione_". **UC22 Aggiungi Mentore** lo realizza (a costo minimo, riusando `assegnaStaff`/`Incarico`).

**Niente di rinviato:** è l'iterazione conclusiva. Eventuali raffinamenti (azioni disciplinari forti, autenticazione di sicurezza reale, rimborsi/retry del pagamento) sono elencati come **problemi aperti** (§4.5), non come scope.

### 1.5 Integrazione con il Sistema di Pagamento (contratto fissato)

`progetto.MD` (§Sistema di Pagamento): "_servizio esterno utilizzato per **erogare** il premio in denaro al team vincitore._" Per simmetria con l'integrazione Calendar (iter. 3, §1.5):

- **Isolamento**: l'accesso al Sistema di Pagamento passa **solo** da un **Adapter** `PaymentGateway` (pattern Adapter, stessa famiglia di `CalendarGateway`); i `service` non chiamano direttamente l'API esterna.
- **Contratto sincrono**: la piattaforma invoca l'erogazione e attende l'esito (riferimento di pagamento / errore). Gli errori sono espliciti nelle estensioni (servizio non disponibile, erogazione rifiutata).
- **Idempotenza**: un hackathon il cui premio è già stato erogato **non** viene ri-pagato (registrazione del `riferimentoPagamento`).

| Caso d'uso           | Interazione con il Sistema di Pagamento                               |
| -------------------- | --------------------------------------------------------------------- |
| UC16 Paga Montepremi | **eroga** il premio al team vincitore (addebito/transazione sincrona) |

### 1.6 Decisioni di progettazione fissate (analisi "council")

- **D1 — Secondo sistema esterno via Adapter.** L'erogazione del premio è delegata al Sistema di Pagamento attraverso `PaymentGateway` (**Adapter**), esattamente come Calendar in iter. 3. Così **entrambi** i sistemi esterni di `progetto.MD` sono isolati dalla stessa famiglia di pattern (coerenza architetturale, non un terzo pattern nuovo).
- **D2 — Stato di `Segnalazione` e `Pagamento` = enum semplice** (`StatoSegnalazione`, `StatoPagamento`), **NON** un pattern State: il GoF State resta riservato all'Hackathon, coerente con `Invito`/`RichiestaSupporto`/`PropostaCall` (iter. 2-3).
- **D3 — Pre-condizione di pagamento.** Si paga **solo** un hackathon **"Concluso"** con **vincitore proclamato** (UC08) e **non già pagato**. Lo stato "Concluso" ha tutte le guard a falso ma **non** blocca il pagamento (riguarda un'azione diversa — già annotato in iter. 2 UC08).
- **D4 — Autenticazione "sottile".** UC19 (registrazione) e UC20 (accesso) rendono **concreto** il passaggio Visitatore → Utente, finora trattato come pre-condizione stub (§4.1 delle iterazioni precedenti). **Non** si realizza uno strato di sicurezza completo (hashing robusto, sessioni, ruoli a runtime): si modella il minimo che collega l'attore Visitatore e dà un'identità reale all'"utente corrente". La sicurezza forte resta fuori scope (§4.5).
- **D5 — Scope dell'Organizzatore via `Incarico`.** In UC18 e UC22 l'Organizzatore vede/agisce **solo** sugli hackathon a cui è assegnato (Incarico, ruolo = Organizzatore) — stessa convenzione di scope dei Mentori (iter. 3, D3).
- **D6 — Riuso Observer.** Le notifiche di segnalazione (UC17 → Organizzatore) riusano `ServizioNotifiche` (Subject) / `Observer`; `Segnalazione` è un nuovo tipo di **`Notifica`** (marker introdotto in iter. 3 per generalizzare il payload).

---

## 2. Specifiche dei casi d'uso

### UC16 — Paga Montepremi

| Campo                  | Valore                                     |
| ---------------------- | ------------------------------------------ |
| **Nome**               | Paga Montepremi                            |
| **ID**                 | UC16                                       |
| **Portata**            | HackHub (sistema)                          |
| **Livello**            | Obiettivo utente                           |
| **Attore primario**    | Organizzatore                              |
| **Attori di supporto** | **Sistema di Pagamento** (sistema esterno) |
| **Rank / Complessità** | Alta / Media                               |

**Breve descrizione.** A hackathon concluso, l'Organizzatore **eroga il premio in denaro al team vincitore**; il pagamento è **delegato al Sistema di Pagamento** (transazione sincrona). Chiude la catena del valore avviata con la proclamazione del vincitore (UC08).

**Evento scatenante.** L'hackathon è concluso con un vincitore proclamato e l'Organizzatore deve corrispondere il montepremi.

**Parti interessate e interessi.**

- _Organizzatore_: vuole erogare il premio in modo tracciabile e una sola volta.
- _Team vincitore_: vuole ricevere il montepremi.
- _Sistema di Pagamento_: esegue e registra la transazione.

**Pre-condizioni.**

- L'attore è autenticato ed è **Organizzatore assegnato** all'hackathon (Incarico, ruolo Organizzatore). _(rimando: Autenticazione, §4.1)_
- L'hackathon è nello stato **"Concluso"** ed ha un **vincitore** registrato (`Hackathon.vincitore`, UC08).
- Il premio **non è già stato erogato** (nessun pagamento completato per quell'hackathon).

**Post-condizioni (garanzia di successo).**

- Esiste un **`Pagamento`** in stato **"Completato"** verso il team vincitore, con il **riferimento di transazione** restituito dal Sistema di Pagamento.
- _Oppure_ l'erogazione fallisce / è annullata e nessun pagamento risulta completato.

**Scenario principale di successo.**

1. L'Organizzatore seleziona un proprio hackathon "Concluso" e richiede di erogare il montepremi.
2. Il sistema verifica che esista un vincitore e che il premio non sia già stato erogato.
3. Il sistema, tramite `PaymentGateway`, **eroga** l'importo del montepremi al team vincitore sul **Sistema di Pagamento** (sincrono).
4. Il sistema crea il `Pagamento` (stato "Completato") e registra il riferimento di transazione.
5. Il sistema conferma l'avvenuta erogazione.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **1a. L'hackathon non è "Concluso" o non ha un vincitore:**
  - 1a.1 Il sistema rifiuta: il premio si eroga solo a hackathon concluso con vincitore.
  - 1a.2 exit Scenario.
- **2a. Il premio è già stato erogato** (pagamento completato esistente):
  - 2a.1 Il sistema segnala il duplicato e non ri-eroga (idempotenza).
  - 2a.2 exit Scenario.
- **3a. Sistema di Pagamento non disponibile** (errore del servizio esterno):
  - 3a.1 Il sistema segnala che l'erogazione non è possibile ora e invita a riprovare; nessun `Pagamento` completato.
  - 3a.2 exit Scenario.
- **3b. Erogazione rifiutata** (fondi/credenziali/transazione respinta):
  - 3b.1 Il sistema registra il tentativo come "Fallito" e segnala il motivo.
  - 3b.2 exit Scenario.
- **\*a. In qualunque momento l'Organizzatore annulla:** nessuna erogazione.

**Regole di business esercitate.**

- L'**erogazione del premio è delegata al Sistema di Pagamento** (`progetto.MD` §Sistema di Pagamento).
- Si paga **un solo** team (il vincitore di UC08) e **una sola volta** (idempotenza).
- Pagamento ammesso **solo** a hackathon "Concluso" con vincitore.

**Requisiti speciali / note implementative.** Nuova entità `Pagamento` con `stato` = **enum** `StatoPagamento` (NON pattern State, §4.3). **Adapter** `PaymentGateway` isola l'API esterna (contratto sincrono, §1.5) — **riuso** della famiglia Adapter già usata per Calendar. Opzionale: **Observer** per notificare il team vincitore dell'avvenuto pagamento (vedi Problemi aperti). Auth: Organizzatore corrente come parametro-stub (o reale, dopo UC19/UC20).

**Frequenza d'uso.** Bassa (una volta per hackathon).

**Problemi aperti.** Importo: si usa `Hackathon.montepremi` (`long`) — gestione valuta/decimali. Destinatario tecnico del bonifico (il team non ha coordinate di pagamento nel modello attuale): a chi è intestata l'erogazione (Amministratore del team?). Rimborsi/retry in caso di fallimento. Notifica Observer al team vincitore (consigliata, simmetrica al mentoring).

**Suggerimento VP.** Collega `Sistema di Pagamento` come supporting actor; Sequence Diagram con la linea di vita `:PaymentGateway` (Adapter) verso `Sistema di Pagamento` (erogazione sincrona). Requirement "Erogazione premio delegata al Sistema di Pagamento".

---

### UC17 — Segnala Team

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Segnala Team      |
| **ID**                 | UC17              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Mentore           |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Media / Bassa     |

**Breve descrizione.** Il Mentore, se **nota una violazione del regolamento** da parte di un team, lo **segnala all'Organizzatore** per le decisioni del caso. Il sistema crea una **`Segnalazione`** e **notifica l'Organizzatore** dell'hackathon (Observer).

**Evento scatenante.** Il Mentore rileva un comportamento scorretto di un team durante l'hackathon (`progetto.MD` §Mentore).

**Parti interessate e interessi.**

- _Mentore_: vuole segnalare la violazione a chi può decidere.
- _Organizzatore_: vuole essere informato per prendere le decisioni del caso (UC18).
- _Team segnalato_: è soggetto alla valutazione dell'Organizzatore.

**Pre-condizioni.**

- L'attore è autenticato ed è **Mentore assegnato** all'hackathon (Incarico, ruolo = Mentore). _(rimando: Autenticazione, §4.1)_
- Esiste il **team da segnalare**, iscritto a quell'hackathon.

**Post-condizioni (garanzia di successo).**

- Esiste una **`Segnalazione`** in stato **"Aperta"** verso (team, hackathon), con la descrizione della violazione.
- L'**Organizzatore** dell'hackathon è stato **notificato** (Observer).
- _Oppure_ l'operazione è annullata e nessuna segnalazione viene creata.

**Scenario principale di successo.**

1. Il Mentore seleziona un team del proprio hackathon e richiede di segnalarlo.
2. Il sistema presenta il modulo (descrizione della violazione).
3. Il Mentore inserisce la descrizione e conferma.
4. Il sistema crea la `Segnalazione` (stato "Aperta") verso (team, hackathon).
5. Il sistema notifica l'Organizzatore dell'hackathon (Observer).
6. Il sistema conferma l'invio della segnalazione.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **1a. Il Mentore non è assegnato all'hackathon del team:**
  - 1a.1 Il sistema rifiuta (fuori competenza — Incarico).
  - 1a.2 exit Scenario.
- **3a. (opzionale) Esiste già una segnalazione "Aperta"** dello stesso Mentore per lo stesso team e hackathon:
  - 3a.1 Il sistema avvisa ed evita il duplicato.
  - 3a.2 jump to 2.
- **\*a. In qualunque momento il Mentore annulla:** nessuna segnalazione creata.

**Regole di business esercitate.**

- Il Mentore può segnalare **solo** i team degli hackathon a cui è **assegnato** (Incarico, ruolo = Mentore).
- La segnalazione è indirizzata all'**Organizzatore** di quell'hackathon, che decide (UC18).

**Requisiti speciali / note implementative.** Nuova entità `Segnalazione` con `stato` = **enum** `StatoSegnalazione` (NON pattern State, §4.3). **Observer** riusato: `Segnalazione` è una `Notifica` verso l'Organizzatore. Auth: Mentore corrente come parametro-stub.

**Frequenza d'uso.** Bassa.

**Problemi aperti.** Gravità/categoria della violazione. Possibilità per il team di replicare. Visibilità della segnalazione al team segnalato.

**Suggerimento VP.** Sequence Diagram: Mentore → Handler → `GestoreSegnalazione` → crea `Segnalazione`; poi `ServizioNotifiche` → Organizzatore. Requirement "Mentore segnala solo i team dei propri hackathon".

---

### UC18 — Gestisci Segnalazione

| Campo                  | Valore                |
| ---------------------- | --------------------- |
| **Nome**               | Gestisci Segnalazione |
| **ID**                 | UC18                  |
| **Portata**            | HackHub (sistema)     |
| **Livello**            | Obiettivo utente      |
| **Attore primario**    | Organizzatore         |
| **Attori di supporto** | nessuno               |
| **Rank / Complessità** | Media / Media         |

**Breve descrizione.** L'Organizzatore **consulta le segnalazioni** relative ai propri hackathon e prende le **decisioni del caso**, ponendo ciascuna segnalazione in stato "Gestita" (con l'esito della decisione) o "Archiviata".

**Evento scatenante.** L'Organizzatore ha ricevuto la notifica di una segnalazione (UC17) e deve decidere.

**Parti interessate e interessi.**

- _Organizzatore_: vuole valutare le segnalazioni e decidere (`progetto.MD` §Mentore: "_per le decisioni del caso_").
- _Mentore segnalante_: vuole che la sua segnalazione sia presa in carico.
- _Team segnalato_: è interessato all'esito.

**Pre-condizioni.**

- L'attore è autenticato ed è **Organizzatore assegnato** ad almeno un hackathon (Incarico). _(rimando: Autenticazione, §4.1)_

**Post-condizioni (garanzia di successo).**

- La `Segnalazione` selezionata è in stato **"Gestita"** (con l'esito della decisione) o **"Archiviata"**.
- _Oppure_ nessuna decisione è presa (sola consultazione) e nulla cambia.

**Scenario principale di successo.**

1. L'Organizzatore richiede l'elenco delle segnalazioni dei propri hackathon.
2. Il sistema recupera le `Segnalazione` degli hackathon a cui l'Organizzatore è assegnato (Incarico) e le mostra (team, hackathon, descrizione, stato).
3. L'Organizzatore seleziona una segnalazione "Aperta" e registra la decisione del caso.
4. Il sistema pone la segnalazione in stato "Gestita" (con l'esito) e ne conserva la decisione.
5. Il sistema conferma la presa in carico.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **2a. Nessuna segnalazione presente** per gli hackathon dell'Organizzatore:
  - 2a.1 Il sistema mostra un elenco vuoto.
- **3a. La segnalazione non è più "Aperta"** (già gestita/archiviata):
  - 3a.1 Il sistema informa che la segnalazione è già stata trattata.
  - 3a.2 exit Scenario.
- **3b. L'Organizzatore archivia** la segnalazione senza provvedimenti:
  - 3b.1 Il sistema pone la segnalazione in stato "Archiviata".
- **\*a. In qualunque momento l'Organizzatore annulla:** nessun cambiamento.

**Regole di business esercitate.**

- L'Organizzatore vede/gestisce **solo** le segnalazioni degli hackathon a cui è **assegnato** (Incarico) — filtro come per i Mentori (iter. 3).
- La decisione del caso è prerogativa dell'**Organizzatore** (`progetto.MD`).

**Requisiti speciali / note implementative.** Riusa l'entità `Segnalazione` (UC17) e il `GestoreSegnalazione`. Information Expert: il sistema sa quali hackathon competono all'Organizzatore (Incarico). Auth: Organizzatore corrente come parametro-stub.

**Frequenza d'uso.** Bassa.

**Problemi aperti.** **Azione disciplinare forte** (es. squalifica del team / esclusione dalla classifica): se realizzata, impatterebbe UC08 (proclamazione) e/o lo stato dell'iscrizione → tenuta **fuori scope** in questa iterazione (la decisione resta un esito testuale). Notifica dell'esito al Mentore segnalante e/o al team.

**Suggerimento VP.** Collega questo UC dopo UC17 (l'Organizzatore tratta la segnalazione ricevuta). Requirement "Organizzatore: decisioni del caso sulle segnalazioni dei propri hackathon".

---

### UC19 — Registrazione

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Registrazione     |
| **ID**                 | UC19              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Visitatore        |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Media / Bassa     |

**Breve descrizione.** Un **Visitatore** crea un account fornendo i propri dati e **diventa Utente** registrato. Realizza in modo **concreto** il passaggio Visitatore → Utente, finora trattato come pre-condizione stub. L'accesso vero e proprio è un caso d'uso distinto (UC20 Autenticazione).

**Evento scatenante.** Il Visitatore vuole partecipare (creare/iscrivere un team, ecc.) e deve prima registrarsi (`progetto.MD` §Visitatore).

**Parti interessate e interessi.**

- _Visitatore_: vuole ottenere un'identità nella piattaforma.
- _Piattaforma_: vuole identità univoche (email non duplicata) per attribuire le azioni.

**Pre-condizioni.**

- Nessuna (il Visitatore è un utente **non autenticato**).

**Post-condizioni (garanzia di successo).**

- Esiste un nuovo **`Utente`** registrato con le credenziali fornite (email univoca).
- _Oppure_ la registrazione è rifiutata (email già in uso / dati non validi) e nessun utente viene creato.

**Scenario principale di successo.**

1. Il Visitatore richiede la registrazione.
2. Il sistema presenta il modulo (nome, email, password).
3. Il Visitatore inserisce i dati e conferma.
4. Il sistema verifica la validità dei dati e che l'email non sia già in uso.
5. Il sistema crea l'`Utente` con le credenziali e conferma la registrazione.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **4a. Email già in uso:**
  - 4a.1 Il sistema rifiuta la registrazione e segnala che l'email è già registrata.
  - 4a.2 jump to 2.
- **4b. Dati non validi** (email malformata / password debole / campi mancanti):
  - 4b.1 Il sistema segnala i campi non validi.
  - 4b.2 jump to 2.
- **\*a. In qualunque momento il Visitatore annulla:** nessun utente creato.

**Regole di business esercitate.**

- L'**email è univoca** (identità dell'utente).
- La registrazione **non** abilita di per sé le funzionalità riservate: serve anche l'accesso (UC20), come da `progetto.MD` §Visitatore.

**Requisiti speciali / note implementative.** **Autenticazione "sottile" (D4)**: si crea l'`Utente` con credenziali; **non** si realizza uno strato di sicurezza completo (hashing forte, sessioni/token, gestione ruoli a runtime), che resta fuori scope (§4.5). Insieme a UC20, questo UC **rende reale l'"utente corrente"** che le iterazioni 1-3 trattavano come parametro-stub.

**Frequenza d'uso.** Media (registrazione una tantum).

**Problemi aperti.** Conservazione sicura della password (hashing/salting). Conferma email. Recupero password.

**Suggerimento VP.** Collega `Visitatore` a questo UC. Requirement "Email univoca in registrazione".

---

### UC20 — Autenticazione

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Autenticazione    |
| **ID**                 | UC20              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Visitatore        |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Media / Bassa     |

**Breve descrizione.** Un **Visitatore** già registrato (UC19) fornisce le proprie credenziali ed **effettua l'accesso**, diventando l'**utente autenticato** corrente; ottiene così l'accesso alle funzionalità riservate.

**Evento scatenante.** Un utente registrato vuole usare una funzionalità riservata e deve prima accedere (`progetto.MD` §Visitatore).

**Parti interessate e interessi.**

- _Visitatore_: vuole accedere con la propria identità.
- _Piattaforma_: vuole attribuire le azioni all'utente corretto e negare l'accesso a credenziali non valide.

**Pre-condizioni.**

- Esiste un **`Utente`** registrato (UC19); l'attore **non è ancora autenticato**.

**Post-condizioni (garanzia di successo).**

- L'utente risulta **autenticato** (accesso effettuato): è l'"utente corrente" della sessione.
- _Oppure_ l'accesso è rifiutato (credenziali non valide) e nessuna sessione è aperta.

**Scenario principale di successo.**

1. Il Visitatore richiede l'accesso.
2. Il sistema presenta il modulo (email, password).
3. Il Visitatore inserisce le credenziali e conferma.
4. Il sistema verifica le credenziali.
5. Il sistema autentica l'utente e conferma l'accesso effettuato.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **4a. Credenziali non valide** (email inesistente / password errata):
  - 4a.1 Il sistema rifiuta l'accesso senza distinguere quale campo è errato.
  - 4a.2 jump to 2.
- **\*a. In qualunque momento il Visitatore annulla:** nessuna sessione aperta.

**Regole di business esercitate.**

- Solo dopo registrazione (UC19) **e** accesso l'utente può usare le funzionalità riservate (`progetto.MD` §Visitatore).
- L'identità è verificata sulle credenziali registrate (email univoca).

**Requisiti speciali / note implementative.** **Autenticazione "sottile" (D4)**: si gestisce un accesso minimale; **non** si realizzano sessioni/token robusti, logout, gestione ruoli a runtime (fuori scope, §4.5). Insieme a UC19, **rende reale l'"utente corrente"** trattato come parametro-stub nelle iterazioni 1-3.

**Frequenza d'uso.** Alta (accesso frequente).

**Problemi aperti.** Sessioni/token e logout (modellabile come UC separato se richiesto). Numero massimo di tentativi / blocco. Recupero password.

**Suggerimento VP.** Collega `Visitatore` a questo UC; modella la dipendenza da UC19 (l'utente dev'essere registrato). Requirement "Accesso richiede credenziali valide".

---

### UC21 — Accedi a informazioni pubbliche

| Campo                  | Valore                          |
| ---------------------- | ------------------------------- |
| **Nome**               | Accedi a informazioni pubbliche |
| **ID**                 | UC21                            |
| **Portata**            | HackHub (sistema)               |
| **Livello**            | Obiettivo utente                |
| **Attore primario**    | Visitatore                      |
| **Attori di supporto** | nessuno                         |
| **Rank / Complessità** | Bassa / Bassa                   |

**Breve descrizione.** Un **Visitatore** (utente non autenticato) **accede liberamente** alle informazioni pubbliche sugli hackathon: elenco e dettagli pubblici (nome, regolamento, date, luogo, premio). È l'unica funzionalità accessibile senza autenticazione.

**Evento scatenante.** Il Visitatore vuole informarsi sugli hackathon disponibili (`progetto.MD` §Visitatore).

**Parti interessate e interessi.**

- _Visitatore_: vuole vedere le informazioni pubbliche senza doversi registrare.
- _Organizzatori/team_: vogliono che gli hackathon siano visibili pubblicamente.

**Pre-condizioni.**

- Nessuna (accesso libero, senza autenticazione).

**Post-condizioni (garanzia di successo).**

- Il sistema mostra le informazioni pubbliche degli hackathon. _(operazione di sola lettura: nessun cambiamento di stato.)_

**Scenario principale di successo.**

1. Il Visitatore richiede l'elenco degli hackathon.
2. Il sistema recupera gli hackathon e ne mostra le informazioni pubbliche (nome, date, luogo, regolamento, premio, stato).
3. _Il Visitatore può selezionare un hackathon per vederne i dettagli pubblici._

**Estensioni (flussi alternativi).**

- **2a. Nessun hackathon presente:**
  - 2a.1 Il sistema mostra un elenco vuoto.
- **3a. Il Visitatore tenta un'azione riservata** (es. iscrivere un team):
  - 3a.1 Il sistema richiede registrazione e accesso (rimando a UC19 / UC20).

**Regole di business esercitate.**

- Il Visitatore accede **solo** alle **informazioni pubbliche** (non a sottomissioni, valutazioni, dati riservati) — `progetto.MD` §Visitatore.
- Nessuna funzionalità riservata è disponibile senza registrazione e accesso.

**Requisiti speciali / note implementative.** Sola lettura. Distingue le **informazioni pubbliche** da quelle riservate (le sottomissioni restano accessibili solo allo staff assegnato — `progetto.MD` §Membro dello Staff). Nessun bisogno di autenticazione: è il caso d'uso che dà finalmente un comportamento all'attore Visitatore.

**Frequenza d'uso.** Alta.

**Problemi aperti.** Quali campi sono "pubblici" esattamente (es. elenco team iscritti sì/no). Filtri/ricerca per stato o data. Questo UC è il gemello "pubblico" delle consultazioni dello staff (Membro dello Staff "_consulta l'elenco di tutti gli hackathon_") — valutare se unificarli.

**Suggerimento VP.** Collega `Visitatore` (e, in generalizzazione, gli attori autenticati) a questo UC. Requirement "Visitatore: solo accesso alle informazioni pubbliche".

---

### UC22 — Aggiungi Mentore

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Aggiungi Mentore  |
| **ID**                 | UC22              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Organizzatore     |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Bassa / Bassa     |

**Breve descrizione.** L'Organizzatore **aggiunge un Mentore** a un hackathon **già creato**, assegnandogli un `Incarico` con ruolo Mentore. Realizza il requisito esplicito di `progetto.MD` (§Organizzatore: "_può aggiungere più Mentori all'hackathon anche successivamente alla sua creazione_").

**Evento scatenante.** L'Organizzatore ritiene necessario coinvolgere altri Mentori dopo la creazione dell'hackathon.

**Parti interessate e interessi.**

- _Organizzatore_: vuole rafforzare lo staff di mentoring.
- _Nuovo Mentore_: viene abilitato ad affiancare i team di quell'hackathon (UC12/UC13/UC17).
- _Team_: beneficiano di più mentori disponibili.

**Pre-condizioni.**

- L'attore è autenticato ed è **Organizzatore assegnato** all'hackathon (Incarico). _(rimando: Autenticazione, §4.1)_
- Esiste l'**utente** da assegnare come Mentore.
- L'hackathon **non è "Concluso"** (aggiungere mentori a evento chiuso non ha effetto utile).

**Post-condizioni (garanzia di successo).**

- Esiste un nuovo **`Incarico`** (ruolo = Mentore) che lega l'utente all'hackathon.
- L'utente è ora **Mentore** di quell'hackathon (abilitato a UC12/UC13/UC17).
- _Oppure_ l'operazione è annullata / rifiutata e nessun incarico viene creato.

**Scenario principale di successo.**

1. L'Organizzatore seleziona un proprio hackathon e richiede di aggiungere un Mentore.
2. Il sistema presenta la ricerca/elenco degli utenti assegnabili.
3. L'Organizzatore seleziona l'utente e conferma.
4. Il sistema crea l'`Incarico` (ruolo = Mentore) per (utente, hackathon).
5. Il sistema conferma l'aggiunta del Mentore.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **1a. L'attore non è Organizzatore dell'hackathon:**
  - 1a.1 Il sistema rifiuta (solo l'Organizzatore assegnato può modificare lo staff).
  - 1a.2 exit Scenario.
- **3a. L'utente è già Mentore di quell'hackathon** (Incarico esistente):
  - 3a.1 Il sistema segnala il duplicato e non crea un secondo incarico.
  - 3a.2 jump to 2.
- **3b. L'hackathon è "Concluso":**
  - 3b.1 Il sistema rifiuta: non si modifica lo staff di un hackathon concluso.
  - 3b.2 exit Scenario.
- **\*a. In qualunque momento l'Organizzatore annulla:** nessun incarico creato.

**Regole di business esercitate.**

- Solo l'**Organizzatore assegnato** può aggiungere mentori (gestione dello staff).
- I Mentori possono essere aggiunti **anche dopo la creazione** dell'hackathon (`progetto.MD` §Organizzatore).
- Resta valido il vincolo di staff (1 Organizzatore, 1 Giudice, 1..\* Mentori): l'aggiunta **incrementa** i Mentori, non altera Organizzatore/Giudice.

**Requisiti speciali / note implementative.** **Riusa** la logica di `assegnaStaff`/`Incarico` (iter. 1): operazione `aggiungiMentore(hackathon, utente)` sul control dell'hackathon, che crea un `Incarico` (ruolo Mentore). Nessuna nuova entità. Auth: Organizzatore corrente come parametro-stub.

**Frequenza d'uso.** Bassa.

**Problemi aperti.** Rimozione di un Mentore (non richiesta dalla traccia). Limite massimo di mentori. Notifica al nuovo Mentore (Observer, opzionale).

**Suggerimento VP.** Sequence Diagram breve: Organizzatore → Handler → `GestoreHackathon.aggiungiMentore` → crea `Incarico`. Requirement "Aggiunta Mentori anche dopo la creazione".

---

## 3. Concatenamento end-to-end (coerenza pre/post-condizioni e dipendenze)

Questa iterazione **chiude** le catene aperte e collega gli ultimi attori. La post-condizione di un caso d'uso abilita la pre-condizione del successivo.

```
ACCESSO (attore Visitatore, finora scollegato)
  UC21 Accedi a informazioni pubbliche → informazioni pubbliche (nessuna auth)
  UC19 Registrazione            → Visitatore diventa Utente registrato
        │
  UC20 Autenticazione           → Utente autenticato  ⇒ abilita TUTTI gli UC riservati (iter. 1-4)

VALORE (chiusura della catena iniziata in iter. 1-2)
  … UC08 Proclama Vincitore (iter. 2) → Hackathon "Concluso" + vincitore
  UC16 Paga Montepremi          → Pagamento "Completato" al team vincitore   (Adapter: Sistema di Pagamento)

GOVERNANCE (nuovo ciclo disciplinare)
  UC17 Segnala Team             → Segnalazione "Aperta" + notifica Organizzatore   (Observer)
        │
  UC18 Gestisci Segnalazione    → Segnalazione "Gestita"/"Archiviata" (decisioni del caso)

STAFF
  UC22 Aggiungi Mentore         → nuovo Incarico (Mentore)  ⇒ abilita UC12/UC13/UC17 per il nuovo Mentore
```

**Dipendenze chiave.** UC16 presuppone UC08 (hackathon concluso con vincitore). UC18 presuppone UC17 (esiste una segnalazione). UC20 presuppone UC19 (l'utente dev'essere registrato per accedere). UC22 alimenta la catena del mentoring (iter. 3) e la governance (UC17). UC19 + UC20 rendono reale l'"utente corrente" assunto come stub in **tutte** le iterazioni precedenti; UC21 è l'unico UC accessibile **senza** autenticazione.

**Continuità coi pattern.** Lo **State** resta sul solo Hackathon (letto, non transito: UC16 richiede "Concluso", UC22 richiede non-"Concluso"). L'**Observer** (iter. 2-3) è **riusato** per le segnalazioni (UC17). L'**Adapter** (iter. 3, Calendar) è **riusato** per il Sistema di Pagamento (UC16): a fine progetto **entrambi** i sistemi esterni di `progetto.MD` sono isolati dalla stessa famiglia di pattern. **Nessun nuovo pattern**: l'ultima iterazione consolida, non aggiunge.

### 3.1 Matrice di copertura attori/requisiti → casi d'uso (verifica di completezza)

A chiusura delle 4 iterazioni, **ogni capacità esplicita di `progetto.MD` è realizzata da almeno un caso d'uso** e ogni attore è collegato:

| Attore (`progetto.MD`)         | Capacità dichiarata                                 | Caso/i d'uso                             |
| ------------------------------ | --------------------------------------------------- | ---------------------------------------- |
| Membro dello Staff             | consulta l'elenco di tutti gli hackathon            | UC21 (generalizzazione agli autenticati) |
| Membro dello Staff             | accede alle sottomissioni (hackathon assegnati)     | UC05 (Giudice), UC08 (Organizzatore) ¹   |
| Organizzatore                  | crea hackathon                                      | UC01                                     |
| Organizzatore                  | avvia / inizia fase di valutazione                  | UC06 / UC07                              |
| Organizzatore                  | proclama l'unico vincitore (a valutazioni complete) | UC08                                     |
| Organizzatore                  | aggiunge Mentori anche dopo la creazione            | **UC22**                                 |
| Organizzatore                  | eroga il premio al vincitore                        | **UC16**                                 |
| Organizzatore                  | gestisce le segnalazioni (decisioni del caso)       | **UC18**                                 |
| Mentore                        | visualizza le richieste di supporto                 | UC12                                     |
| Mentore                        | propone una call (prenotazione via Calendar)        | UC13 (+ UC14 / UC15)                     |
| Mentore                        | segnala un team all'Organizzatore                   | **UC17**                                 |
| Giudice                        | valuta le sottomissioni (giudizio + 0-10)           | UC05                                     |
| Membro del Team                | consulta gli hackathon                              | UC21                                     |
| Membro del Team                | iscrive il proprio team                             | UC03                                     |
| Membro del Team                | invia/aggiorna la sottomissione entro scadenza      | UC04                                     |
| Membro del Team                | richiede supporto                                   | UC11                                     |
| Utente                         | crea un team invitando altri utenti                 | UC02 + UC09                              |
| Utente                         | accetta un invito (vincolo "un solo team")          | UC10                                     |
| Utente                         | si registra e accede                                | **UC19** + **UC20**                      |
| Amministratore Team            | accetta / rifiuta una proposta di call              | UC14 / UC15                              |
| Visitatore                     | consulta le informazioni pubbliche                  | **UC21**                                 |
| Visitatore                     | registrazione e accesso                             | **UC19** + **UC20**                      |
| Calendar (esterno)             | prenotazione/conferma slot                          | UC13, UC14                               |
| Sistema di Pagamento (esterno) | erogazione del premio                               | **UC16**                                 |

> ¹ L'accesso in lettura alle sottomissioni è **esercitato dai ruoli che ne hanno l'obiettivo** (Giudice per valutare, UC05; Organizzatore per la classifica/proclamazione, UC08). Una vista read-only dedicata "Consulta Sottomissioni" per un qualunque Membro dello Staff (es. il Mentore) è un **raffinamento opzionale**, non realizzato come UC a sé per non duplicare UC05/UC08 (vedi §4.5).

> **Esito.** Nessun requisito esplicito di `progetto.MD` resta scoperto; tutti gli attori (inclusi i due sistemi esterni) sono collegati. La copertura è completa a livello di casi d'uso.

---

## 4. Appendice

### 4.1 Da pre-condizione trasversale a caso d'uso reale (Registrazione / Autenticazione)

Nelle iterazioni 1-3, ogni caso d'uso autenticato presupponeva **Registrazione** e **Autenticazione** come **pre-condizione/«include»** non specificata, con l'utente corrente trattato come **parametro-stub** (§4.1 dei documenti precedenti). **UC19 e UC20 chiudono questo debito**: la registrazione (UC19) e l'accesso (UC20) diventano due casi d'uso reali e distinti (attore Visitatore), e l'"utente corrente" acquisisce un'identità concreta. La sicurezza forte (hashing, sessioni/token, ruoli a runtime) resta deliberatamente fuori scope (**autenticazione "sottile"**, D4) — vedi §4.5.

### 4.2 Contesto implementativo (vincoli di `progetto.MD`)

- Sviluppo in **Java**, poi **Spring Boot**; presentazione anche solo CLI e/o **API REST**.
- **Almeno due design pattern**: ampiamente soddisfatto (State + Observer + Adapter). Questa iterazione **non aggiunge** pattern: **riusa** Adapter (Sistema di Pagamento) e Observer (segnalazioni). A fine progetto i pattern in gioco sono **tre famiglie** (comportamentale State, comportamentale Observer, strutturale Adapter), con i **due sistemi esterni** entrambi isolati da Adapter.

### 4.3 Impatti sul modello di dominio e di progettazione (delta da portare nei file `*CHANGE`)

Da portare nei diagrammi di analisi/progettazione di iter. 4 come file `*CHANGE` (non toccare i `*REALIZZATI`):

- **`Pagamento`** (nuova entità): `importo`, `dataPagamento`, `riferimentoPagamento` (id transazione esterna), `stato : StatoPagamento`. Associazioni: `Hackathon` (premio erogato) e `Team` (vincitore beneficiario).
- **`Segnalazione`** (nuova entità): `descrizione`, `dataSegnalazione`, `stato : StatoSegnalazione`. Associazioni: `Utente`/Mentore (segnalante, via Incarico), `Team` (segnalato), `Hackathon` (contesto). È una **`Notifica`** (Observer) verso l'Organizzatore.
- **Credenziali su `Utente`**: aggiungere `password` (o entità `Credenziale`) per UC19/UC20; `RepoUtente.esisteEmail(email)` / `trovaPerEmail(email)`.
- **Enum**: `StatoPagamento { Inviato, Completato, Fallito }`, `StatoSegnalazione { Aperta, Gestita, Archiviata }` — **enum semplici, NON pattern State**.
- **`PaymentGateway`** (**Adapter**, dominio della soluzione): porta della piattaforma (`erogaPremio(team, importo) : riferimentoPagamento`) + adapter concreto verso l'API esterna `SistemaPagamento` (Adaptee, «sistema esterno»). I `service` usano **solo** la porta — simmetrico a `CalendarGateway`.
- **`Hackathon`**: operazione `aggiungiMentore(utente)` (crea `Incarico` ruolo Mentore) — riuso della logica di `assegnaStaff`.
- **Observer** (riuso): `ServizioNotifiche` (Subject) notifica l'`Utente`/Organizzatore (Observer) per le segnalazioni; `Segnalazione` realizza `Notifica`.
- **EBC/persistenza** coerenti con `CONVENZIONI_DIAGRAMMI.md`: `Handler*` per-UC (`HandlerPagaMontepremi`, `HandlerSegnalaTeam`, `HandlerGestisciSegnalazione`, `HandlerRegistrazione`, `HandlerAutenticazione`, `HandlerAccediInformazioniPubbliche`, `HandlerAggiungiMentore`), `service` (`GestorePagamento`, `GestoreSegnalazione`, `GestoreAccount` (registrazione + autenticazione), e riuso `GestoreHackathon` per consultazione/aggiungiMentore), `Repo*` (`RepoPagamento`, `RepoSegnalazione`, riuso `RepoUtente`/`RepoHackathon`). **Nota:** gli `Incarico` (UC22) sono parte dell'aggregato `Hackathon` (creati da `assegnaStaff`/`aggiungiMentore`) e si persistono via `RepoHackathon` — **non** esiste un `RepoIncarico` dedicato.

### 4.4 Pattern per caso d'uso

| UC   | Pattern in evidenza                                           | Note                                                       |
| ---- | ------------------------------------------------------------- | ---------------------------------------------------------- |
| UC16 | **Adapter** (Sistema di Pagamento), Creator [+ Observer opz.] | eroga il premio; crea `Pagamento`; secondo sistema esterno |
| UC17 | **Observer** (notifica), Creator, Controller                  | crea `Segnalazione`; notifica l'Organizzatore              |
| UC18 | Information Expert, Controller                                | scope via `Incarico`; cambia stato della segnalazione      |
| UC19 | Creator, Controller                                           | crea `Utente` (registrazione); autenticazione "sottile"    |
| UC20 | Controller                                                    | verifica credenziali; accesso "sottile"                    |
| UC21 | Information Expert, Controller                                | sola lettura; informazioni pubbliche                       |
| UC22 | Creator (Incarico), Controller                                | riuso `assegnaStaff`; nessuna nuova entità                 |

> **Copertura pattern iter. 4:** si **riusano** Adapter (Pagamento) e Observer (segnalazioni); lo **State** è solo letto (pre-condizioni di UC16/UC22). Nessun nuovo pattern — l'ultima iterazione consolida la narrativa architetturale.

### 4.5 Problemi aperti consolidati (decisioni da confermare)

1. **Autenticazione di sicurezza reale**: UC19/UC20 sono "sottili" (D4); hashing password, sessioni/token, logout, recupero password restano fuori scope. Logout modellabile come UC separato se richiesto.
2. **Destinatario tecnico del pagamento** (UC16): il team non ha coordinate di pagamento nel modello; a chi è intestata l'erogazione (Amministratore?). Valuta/decimali del `montepremi`. Rimborsi/retry.
3. **Azione disciplinare forte** (UC18): squalifica del team / esclusione dalla classifica impatterebbe UC08 e lo stato dell'iscrizione → tenuta fuori scope (la decisione resta esito testuale).
4. **Ciclo di vita della `Segnalazione`** (`Aperta → Gestita → Archiviata`): chi e quando archivia; notifica dell'esito al Mentore/team.
5. **Notifiche Observer opzionali**: pagamento al team vincitore (UC16), incarico al nuovo Mentore (UC22) — consigliate per simmetria, non obbligatorie.
6. **Confine "informazioni pubbliche"** (UC21): quali campi sono pubblici; unificazione con la consultazione dello staff (Membro dello Staff "_consulta l'elenco di tutti gli hackathon_").

### 4.6 Riferimenti

- `progetto.MD` — §Organizzatore (aggiunge Mentori dopo la creazione; proclama il vincitore), §Mentore (segnala il team all'Organizzatore per le decisioni del caso), §Visitatore (consulta info pubbliche; registrazione e accesso per il resto), §Sistema di Pagamento (esterno, eroga il premio al vincitore).
- `docs/02_Processo.pdf` — UP iterativo/incrementale, risk-driven, architecture-centric (iterazione di chiusura).
- `docs/UML_02_CasiUso.pdf` — formato dei casi d'uso (slide 11-15), esempio dettagliato (26-30), attori secondari/«include» (22).
- `docs/Design Patterns.pptx.pdf` — **Adapter** (strutturale, integrazione con sistemi esterni) e **Observer** (notifiche).
- `docs/PatternsGRASP.pdf` — Controller, Creator, Information Expert, Pure Fabrication.
- `iterazione3/useCase/CASI_USO_DETTAGLIATI_REALIZZATI.md` — formato di riferimento e contratto Adapter (Calendar) da imitare per il Sistema di Pagamento; `CONVENZIONI_DIAGRAMMI.md` — convenzioni dei diagrammi.
- `iterazione1/useCase/useCase.puml` — modello dei casi d'uso (attori Organizzatore/Mentore/Visitatore/Sistema di Pagamento e relazioni).
