# Casi d'Uso Dettagliati — Seconda Iterazione HackHub

## 1. Scopo e come usare questo documento con Visual Paradigm 17.3

Questo file contiene la **specifica in formato dettagliato** ("fully-dressed", stile Larman — cfr. esempio _Elabora Vendita_ in `docs/UML_02_CasiUso.pdf` slide 26-30) dei casi d'uso della **seconda iterazione** di HackHub. È il gemello, per l'iterazione 2, di `iterazione1/legacy/CASI_USO_DETTAGLIATI.md`: stessa struttura, stesse convenzioni, stessa mappatura ai campi di Visual Paradigm.

La seconda iterazione **chiude il ciclo di vita dell'hackathon** (rende espliciti i passaggi di stato lasciati automatici nell'iterazione 1) e **rende reali i team** (introduce inviti e accettazioni, finora rinviati). I casi d'uso sono raggruppati nei due "Tier" decisi nell'analisi di selezione:

| ID   | Caso d'uso              | Attore primario     | Tier | Pattern in evidenza                          |
| ---- | ----------------------- | ------------------- | ---- | -------------------------------------------- |
| UC06 | Avvia Hackathon         | Organizzatore       | 1    | **State** (transizione)                      |
| UC07 | Inizia Fase Valutazione | Organizzatore       | 1    | **State** (transizione)                      |
| UC08 | Proclama Vincitore      | Organizzatore       | 1    | **State** (transizione) + Information Expert |
| UC09 | Invita Membro           | Amministratore Team | 2    | **Observer** (notifica)                      |
| UC10 | Accetta Invito          | Utente              | 2    | Information Expert (invariante)              |

> Gli ID proseguono la numerazione dell'iterazione 1 (UC01-UC05). `Registrazione` e `Autenticazione` (attore Visitatore) restano una **pre-condizione trasversale** non specificata in dettaglio (vedi §4.1), come nell'iterazione 1.

### 1.1 Mappatura sezione → campo di Visual Paradigm

In VP: seleziona il caso d'uso nel diagramma → tasto destro → **Open Use Case Details** (oppure doppio clic nella vista UeXceler). La descrizione breve sta invece in **Open Specification ▸ Documentation**.

| Sezione di questo documento                      | Dove inserirla in Visual Paradigm 17.3                    |
| ------------------------------------------------ | --------------------------------------------------------- |
| Nome                                             | Nome del caso d'uso                                       |
| ID                                               | Prefisso nel nome (`UC06 …`) **oppure** un _Tagged Value_ |
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

- **Flow of Events**: inserisci i passi del flusso base uno per riga (VP li numera da solo). Per le ramificazioni usa gli operatori **if / while / jump**; per richiamare un caso d'uso incluso usa **"Referenced Use Case"**.
- **Generazione automatica**: dal Flow of Events VP può generare uno **scheletro di Sequence Diagram** o di **Activity Diagram** — comodo per passare dalla specifica alla progettazione delle interazioni.
- **State Machine Diagram**: questa iterazione rende **esplicite** le transizioni dell'hackathon (UC06-UC08); è il momento giusto per disegnare in VP il **diagramma di macchina a stati** dell'Hackathon (in iscrizione → in corso → in valutazione → concluso) con i _trigger_ corrispondenti a questi casi d'uso. Nell'iterazione 1 le transizioni erano implicite e non disegnabili come trigger.
- **Requirements**: crea un Requirement per ogni regola di business (es. _"vincitore = punteggio massimo"_, _"un utente un solo team"_) e collegalo al caso d'uso, così la regola è tracciabile.
- **Test Plan**: deriva i casi di test dalle estensioni (un test per ogni flusso alternativo).

### 1.3 Convenzioni di scrittura (cfr. `docs/UML_02_CasiUso.pdf` slide 13-15)

- Passi **numerati**, in **forma attiva**: `N. Il <attore/sistema> <azione>` (mai forma passiva).
- Il **primo passo** è sempre un'azione dell'attore primario (attivazione).
- Ramificazioni con la parola **"Se"** e indici annidati (`x.1`, `x.2`).
- **Estensioni** etichettate con il numero del passo + lettera (`3a`, `4a`); `*a` = in qualunque momento; un'estensione non dovrebbe avere a sua volta estensioni.
- **Ordine delle estensioni d'errore**: prima la condizione negativa/di errore (guard breve + rifiuto), poi l'happy path; coerente con la convenzione `alt`/`opt` dei diagrammi di sequenza in `CLAUDE.md` (questo riduce la distanza visiva guard↔esito quando si genera il sequence diagram).

### 1.4 Selezione dei casi d'uso di questa iterazione (Tier 1 + Tier 2)

La selezione segue la metodologia del **Processo Unificato** già usata nell'iterazione 1 (`iterazione1/legacy/ITERAZIONE_1_CASI_USO.md`): iterativo/incrementale, **use-case driven**, **architecture-centric**, **risk-driven**; criteri di ranking (Larman): **copertura architetturale**, **valore di business**, **riduzione del rischio**.

L'iterazione 1 ha consegnato lo **scheletro eseguibile** (UC01-UC05: Crea Hackathon → Crea Team → Iscrive Team → Invia Sottomissione → Valuta Sottomissione) con i pattern **State (GoF)** + **GRASP**, architettura **EBC** (Boundary→Control→Entity) + **Repository**. Restano però due debiti che questa iterazione salda:

1. **Il pattern State è "guard-only".** Gli oggetti-stato implementano solo `iscrizioneConsentita()/sottomissioneConsentita()/valutazioneConsentita()`; **le transizioni non esistono**: l'iterazione 1 faceva avanzare il ciclo "automaticamente in base alle date" (stub, cfr. `iterazione1/legacy/CASI_USO_DETTAGLIATI.md` §1.4). **UC06/UC07/UC08** rendono le transizioni **casi d'uso espliciti** dell'Organizzatore e completano il GoF State (gli stati non solo _rispondono_, ma fanno _avanzare_ il context). Questo è il contenuto **architetturale/di rischio** dell'iterazione.
2. **Il team non ha membri reali.** Il flusso di invito era stato rinviato (cfr. `iterazione1/diagrammiSequenza/RELAZIONE_REALIZZAZIONE.md` §UC02): oggi un team contiene **solo il creatore**. **UC09/UC10** introducono inviti e accettazioni e portano i team a dimensione reale; UC09 è anche la sede naturale del **secondo pattern GoF (Observer)** già indicato come candidato futuro (RELAZIONE §5.2).

**Dipendenza che lega i due Tier.** Con l'invariante "un utente, un solo team" e senza inviti, ogni team avrebbe **esattamente un membro**: _Proclama Vincitore_ (UC08) classificherebbe sottomissioni di team mono-fondatore e l'intera narrativa "hackathon a squadre" non esisterebbe. Perciò **UC09/UC10 vanno realizzati nella stessa iterazione** e, nella dimostrazione, **precedono logicamente** UC08 (che assume team multi-membro). Questo è il motivo per cui Tier 1 e Tier 2 stanno insieme.

### 1.5 Ciclo di vita dell'hackathon in questa iterazione (transizioni esplicite)

A differenza dell'iterazione 1 (avanzamento automatico per data), qui **le transizioni sono casi d'uso espliciti** dell'Organizzatore. La data prevista resta nel modello come **guardia temporale / precondizione** (non come attore automatico concorrente): elimina la contraddizione "due meccanismi per la stessa transizione".

| Transizione                       | Caso d'uso (attore)                     | Guardia temporale (precondizione)                      |
| --------------------------------- | --------------------------------------- | ------------------------------------------------------ |
| (creazione) → **in iscrizione**   | _Crea Hackathon_ (UC01, iter. 1)        | —                                                      |
| **in iscrizione** → **in corso**  | **UC06 Avvia Hackathon** (Org.)         | `now ≥ scadenzaIscrizioni` (coerente con iter. 1 §1.4) |
| **in corso** → **in valutazione** | **UC07 Inizia Fase Valutazione** (Org.) | `now ≥ dataFine` (= scadenza sottomissioni)            |
| **in valutazione** → **concluso** | **UC08 Proclama Vincitore** (Org.)      | tutte le sottomissioni valutate                        |

> **Change log (obbligatorio).** Lo stub di avanzamento automatico-per-data dell'iterazione 1 va **rimosso e degradato a precondizione** delle nuove transizioni esplicite. Tenere entrambi i meccanismi renderebbe il modello dinamico incoerente (due trigger per una transizione) — un esaminatore lo segnalerebbe. Le modifiche al modello vanno nei file `*CHANGE` (vedi §4.3), mai nei `*REALIZZATI`.

### 1.6 Decisioni di progettazione fissate (analisi "council")

Le caratteristiche dei 5 casi d'uso sono state definite risolvendo quattro decisioni aperte (consenso/ dissenso sintetizzati):

- **D1 — Modello di trigger del ciclo di vita.** _Manuale con guardia temporale_: l'Organizzatore esegue la transizione (UC reale + transizione State reale), ammessa solo **a/dopo** la data prevista. Lo stub automatico-per-data è rimosso. _(dissenso: "manuale puro con date solo informative" — più semplice ma indebolisce il requisito "sottomissioni entro la scadenza".)_
- **D2 — Selezione del vincitore (UC08).** Vincitore = **punteggio massimo**; **parità risolta dall'Organizzatore** (un solo vincitore, `progetto.MD`); proclamazione solo a **valutazioni complete**; richiede l'aggiunta dell'attributo `Hackathon.vincitore`. _(alternativa deterministica: parità → sottomissione con `dataOra` più antica, evita l'interazione di scelta.)_
- **D3 — Invito (UC09).** Invita solo l'**Amministratore Team**; si introduce l'entità **`Invito`** (stato `Pendente/Accettato/Rifiutato`, **enum semplice, non un pattern State**); le verifiche vincolanti sono **all'accettazione**; **Observer** notifica l'utente invitato.
- **D4 — Accettazione (UC10).** Verifica autoritativa dell'invariante **"un utente, un solo team"** all'accettazione (chiude la finestra TOCTOU) **e** della **dimensione massima** se il team è iscritto a un hackathon; il nuovo membro non è amministratore; gli altri inviti pendenti restano (non più accettabili per l'invariante).

---

## 2. Specifiche dei casi d'uso

### UC06 — Avvia Hackathon

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Avvia Hackathon   |
| **ID**                 | UC06              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Organizzatore     |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Media / Bassa     |

**Breve descrizione.** L'Organizzatore avvia un hackathon che si trova nello stato **"in iscrizione"**, facendolo transire a **"in corso"**: chiude le iscrizioni e apre le sottomissioni. È il primo dei tre casi d'uso che rendono **esplicite** le transizioni di stato (in iterazione 1 erano automatiche, cfr. §1.5).

**Evento scatenante.** È giunta (o superata) la scadenza delle iscrizioni e l'Organizzatore vuole far partire l'evento.

**Parti interessate e interessi.**

- _Organizzatore_: vuole avviare l'hackathon al momento giusto.
- _Team iscritti_: vogliono iniziare a lavorare e poter sottomettere.
- _Utenti non ancora iscritti_: l'avvio chiude la finestra delle iscrizioni.

**Pre-condizioni.**

- L'attore è autenticato ed è **Organizzatore assegnato** all'hackathon (Incarico con ruolo Organizzatore). _(rimando: Autenticazione, §4.1)_
- L'hackathon è nello stato **"in iscrizione"**.
- È stata raggiunta la **scadenza iscrizioni** (`now ≥ scadenzaIscrizioni`).

**Post-condizioni (garanzia di successo).**

- L'hackathon è nello stato **"in corso"**.
- Le iscrizioni sono chiuse (`iscrizioneConsentita() = falso`); le sottomissioni sono aperte (`sottomissioneConsentita() = vero`).

**Scenario principale di successo.**

1. L'Organizzatore seleziona un proprio hackathon nello stato "in iscrizione".
2. L'Organizzatore richiede l'avvio dell'hackathon.
3. Il sistema verifica che lo stato sia "in iscrizione" e che la scadenza iscrizioni sia raggiunta.
4. Il sistema fa transire l'hackathon allo stato "in corso" (la transizione è **delegata allo stato corrente** — pattern State).
5. Il sistema conferma l'avvio (iscrizioni chiuse, sottomissioni aperte).

**Estensioni (flussi alternativi).**

- **3a. La scadenza iscrizioni non è ancora raggiunta** (avvio prematuro):
  - 3a.1 Il sistema rifiuta l'avvio e indica la data a partire dalla quale è possibile avviare.
  - 3a.2 exit Scenario.
- **3b. L'hackathon non è nello stato "in iscrizione"** (già avviato o oltre):
  - 3b.1 Il sistema rifiuta: la transizione non è consentita dallo stato corrente (per polimorfismo).
  - 3b.2 exit Scenario.
- **3c. (opzionale) Nessun team iscritto:**
  - 3c.1 Il sistema avvisa che non ci sono team iscritti e chiede conferma.
  - 3c.2 L'Organizzatore conferma (jump to 4) oppure annulla.
- **\*a. In qualunque momento l'Organizzatore annulla:** nessuna transizione.

**Regole di business esercitate.**

- La transizione **"in iscrizione → in corso"** avviene solo a/dopo la **scadenza iscrizioni** (preserva la finestra delle iscrizioni).
- L'avvio **chiude le iscrizioni** e **apre le sottomissioni**.
- È un'azione **esplicita** dell'Organizzatore (non più automatica — §1.5).

**Requisiti speciali / note implementative.** La transizione è realizzata dal **pattern State** (lo stato corrente conosce verso quale stato evolvere — Single Responsibility + Open/Closed); controllo dello stato **lato server**. Richiede l'aggiunta dei metodi di transizione assenti in iterazione 1 (vedi §4.3). Auth non realizzata: l'Organizzatore corrente entra come parametro-stub.

**Frequenza d'uso.** Bassa (una volta per hackathon).

**Problemi aperti.** Avviare con **0 team iscritti**: consentito con avviso (default 3c) o bloccato? Se `scadenzaIscrizioni` e `dataInizio` sono distinte, qui si usa `scadenzaIscrizioni` come trigger (coerente con iter. 1 §1.4); valutare se ancorare invece a `dataInizio`.

**Suggerimento VP.** Modella il passo 4 come messaggio all'oggetto-stato; genera un Sequence Diagram (Organizzatore → Handler → Service → Hackathon → Stato). Aggiungi questa transizione anche allo **State Machine Diagram** dell'Hackathon. Crea un Requirement "Transizione esplicita in iscrizione → in corso".

---

### UC07 — Inizia Fase Valutazione

| Campo                  | Valore                  |
| ---------------------- | ----------------------- |
| **Nome**               | Inizia Fase Valutazione |
| **ID**                 | UC07                    |
| **Portata**            | HackHub (sistema)       |
| **Livello**            | Obiettivo utente        |
| **Attore primario**    | Organizzatore           |
| **Attori di supporto** | nessuno                 |
| **Rank / Complessità** | Media / Bassa           |

**Breve descrizione.** L'Organizzatore chiude le sottomissioni e apre la valutazione, facendo transire l'hackathon da **"in corso"** a **"in valutazione"**. Da questo momento il Giudice può valutare (UC05, iter. 1).

**Evento scatenante.** È giunta (o superata) la data di fine dell'hackathon e l'Organizzatore vuole aprire la fase di giudizio.

**Parti interessate e interessi.**

- _Organizzatore_: vuole chiudere le consegne e avviare la valutazione.
- _Membri del team_: la chiusura blocca ulteriori invii/aggiornamenti (la consegna deve essere "entro la scadenza").
- _Giudice_: vuole poter iniziare a valutare.

**Pre-condizioni.**

- L'attore è autenticato ed è **Organizzatore assegnato** all'hackathon. _(rimando: Autenticazione, §4.1)_
- L'hackathon è nello stato **"in corso"**.
- È stata raggiunta la **data di fine** (`now ≥ dataFine`).

**Post-condizioni (garanzia di successo).**

- L'hackathon è nello stato **"in valutazione"**.
- Le sottomissioni sono chiuse (`sottomissioneConsentita() = falso`); la valutazione è aperta (`valutazioneConsentita() = vero`).

**Scenario principale di successo.**

1. L'Organizzatore seleziona un proprio hackathon nello stato "in corso".
2. L'Organizzatore richiede l'inizio della fase di valutazione.
3. Il sistema verifica che lo stato sia "in corso" e che la data di fine sia raggiunta.
4. Il sistema fa transire l'hackathon allo stato "in valutazione" (delega allo stato corrente — pattern State).
5. Il sistema conferma (sottomissioni chiuse, valutazione aperta).

**Estensioni (flussi alternativi).**

- **3a. La data di fine non è ancora raggiunta** (chiusura prematura delle sottomissioni):
  - 3a.1 Il sistema rifiuta: chiudere prima della scadenza violerebbe la regola "sottomissioni entro la scadenza" (`progetto.MD` §Membro del Team).
  - 3a.2 exit Scenario.
- **3b. L'hackathon non è nello stato "in corso":**
  - 3b.1 Il sistema rifiuta (transizione non consentita dallo stato corrente — polimorfismo).
  - 3b.2 exit Scenario.
- **3c. (opzionale) Nessuna sottomissione presente:**
  - 3c.1 Il sistema avvisa che non esistono sottomissioni (l'hackathon si avvierebbe verso una conclusione senza vincitore — vedi UC08 3a).
  - 3c.2 L'Organizzatore conferma (jump to 4) oppure annulla.
- **\*a. In qualunque momento l'Organizzatore annulla:** nessuna transizione.

**Regole di business esercitate.**

- La transizione **"in corso → in valutazione"** non avviene prima della **data di fine** (= scadenza sottomissioni, iter. 1 §1.4): è ciò che dà senso a "consegna entro la scadenza".
- L'inizio valutazione **chiude le sottomissioni** e **abilita UC05** (Valuta Sottomissione).

**Requisiti speciali / note implementative.** Transizione realizzata dal **pattern State**; controllo lato server. Richiede i metodi di transizione (§4.3).

**Frequenza d'uso.** Bassa (una volta per hackathon).

**Problemi aperti.** 0 sottomissioni → si arriva a UC08 senza candidati: come si conclude l'hackathon "senza vincitore"? (vedi UC08, Problemi aperti).

**Suggerimento VP.** Aggiungi la transizione allo State Machine Diagram; Requirement "Chiusura sottomissioni solo a/dopo dataFine".

---

### UC08 — Proclama Vincitore

| Campo                  | Valore             |
| ---------------------- | ------------------ |
| **Nome**               | Proclama Vincitore |
| **ID**                 | UC08               |
| **Portata**            | HackHub (sistema)  |
| **Livello**            | Obiettivo utente   |
| **Attore primario**    | Organizzatore      |
| **Attori di supporto** | nessuno            |
| **Rank / Complessità** | Alta / Media       |

**Breve descrizione.** A valutazioni completate, l'Organizzatore **proclama l'unico team vincitore** (quello con la sottomissione dal punteggio più alto) e l'hackathon transisce a **"concluso"**.

**Evento scatenante.** Il Giudice ha completato le valutazioni e l'Organizzatore deve decretare il vincitore (`progetto.MD` §Organizzatore: "_Quando tutte le sottomissioni di un hackathon sono state giudicate dal Giudice, l'Organizzatore proclama un solo team vincitore_").

**Parti interessate e interessi.**

- _Organizzatore_: vuole proclamare un esito corretto e incontestabile.
- _Team partecipanti_: vogliono conoscere il vincitore.
- _Team vincitore_: ha diritto al montepremi (erogato da _Paga Montepremi_, rinviato).

**Pre-condizioni.**

- L'attore è autenticato ed è **Organizzatore assegnato** all'hackathon. _(rimando: Autenticazione, §4.1)_
- L'hackathon è nello stato **"in valutazione"**.
- Esiste **almeno una sottomissione** e **tutte le sottomissioni** dell'hackathon **sono state valutate** (`Sottomissione.getValutazione() ≠ null` per tutte).

**Post-condizioni (garanzia di successo).**

- L'hackathon ha un **team vincitore registrato** (`Hackathon.vincitore`).
- L'hackathon è nello stato **"concluso"**.

**Scenario principale di successo.**

1. L'Organizzatore seleziona un proprio hackathon nello stato "in valutazione".
2. Il sistema verifica che **tutte** le sottomissioni siano state valutate.
3. Il sistema mostra le sottomissioni (con il team relativo) **ordinate per punteggio** ed evidenzia il punteggio più alto.
4. L'Organizzatore proclama il team vincitore (la sottomissione col punteggio massimo).
5. Il sistema registra il vincitore (`Hackathon.vincitore`) e fa transire l'hackathon a "concluso" (pattern State).
6. Il sistema conferma la proclamazione.

**Estensioni (flussi alternativi).**

- **1a. L'hackathon non è nello stato "in valutazione":**
  - 1a.1 Il sistema impedisce la proclamazione (per polimorfismo) e ne spiega il motivo.
  - 1a.2 exit Scenario.
- **2a. Non tutte le sottomissioni sono state valutate:**
  - 2a.1 Il sistema impedisce la proclamazione ed elenca quante/quali sottomissioni mancano.
  - 2a.2 exit Scenario (l'Organizzatore attende che il Giudice completi — UC05).
- **3a. Nessuna sottomissione esistente** (0 sottomissioni, viola la precondizione "≥ 1 sottomissione"):
  - 3a.1 Il sistema segnala che non esiste alcun vincitore proclamabile.
  - 3a.2 exit Scenario _(la conclusione "senza vincitore" è un problema aperto — vedi sotto)_.
- **4a. Parità sul punteggio massimo** (più sottomissioni a pari punteggio):
  - 4a.1 Il sistema presenta i team a pari merito.
  - 4a.2 L'Organizzatore sceglie **l'unico** team vincitore tra essi (`progetto.MD`: "_un solo team vincitore_") → jump to 5.
- **\*a. In qualunque momento l'Organizzatore annulla:** nessun vincitore, nessuna transizione.

**Regole di business esercitate.**

- **Vincitore = team la cui sottomissione ha il punteggio più alto.**
- **Parità** risolta dall'Organizzatore (deve risultare **un solo** vincitore).
- **Proclamazione consentita solo a valutazioni complete** (`progetto.MD`).
- La proclamazione **conclude** l'hackathon (transizione a "concluso").

**Requisiti speciali / note implementative.**

- Serve un **nuovo attributo** `Hackathon.vincitore: Team` — il modello di iterazione 1 **non lo prevede** (vedi §4.3). Senza di esso UC08 non avrebbe dove scrivere il risultato.
- La verifica "tutte valutate" si effettua su `getValutazione() ≠ null` (Information Expert: la Sottomissione sa se è stata valutata).
- La transizione a "concluso" è realizzata dal **pattern State**.
- **Relazione con Paga Montepremi** (rinviato, Tier 3): è un caso d'uso **separato**. Lo stato "concluso" ha tutte le guard (`iscrizione/sottomissione/valutazione`) a falso, ma ciò **non** blocca il pagamento (riguarda altre azioni); va annotato per non far leggere "concluso" come vicolo cieco.

**Frequenza d'uso.** Bassa (una volta per hackathon).

**Problemi aperti.**

- **Conclusione senza vincitore** (0 sottomissioni): con quale caso d'uso/transizione si chiude l'hackathon? (candidato a una specifica futura).
- **Criterio di parità alternativo**: in alternativa alla scelta dell'Organizzatore (4a), parità → sottomissione con `dataOra` **più antica** (deterministico, nessuna interazione aggiuntiva). Decidere e documentare.

**Suggerimento VP.** Modella il passo 3 (ranking) ed eventualmente la scelta del passo 4/4a con operatori `loop`/`if`; aggiungi la transizione "in valutazione → concluso" allo State Machine Diagram. Crea i Requirement "Vincitore = punteggio massimo" e "Proclamazione richiede tutte le valutazioni".

---

### UC09 — Invita Membro

| Campo                  | Valore              |
| ---------------------- | ------------------- |
| **Nome**               | Invita Membro       |
| **ID**                 | UC09                |
| **Portata**            | HackHub (sistema)   |
| **Livello**            | Obiettivo utente    |
| **Attore primario**    | Amministratore Team |
| **Attori di supporto** | nessuno             |
| **Rank / Complessità** | Media / Media       |

**Breve descrizione.** L'Amministratore di un team invita un altro utente a unirsi al team. Il sistema registra un **invito "pendente"** e **notifica** l'utente invitato (pattern **Observer**). L'accettazione è un caso d'uso separato (UC10). Completa il flusso di invito rinviato da _Crea Team_ (UC02, iter. 1).

**Evento scatenante.** Il team vuole crescere invitando un utente della piattaforma (`progetto.MD` §Utente: "_può creare un nuovo team invitando altri utenti_").

**Parti interessate e interessi.**

- _Amministratore Team_: vuole comporre la squadra.
- _Utente invitato_: vuole ricevere ed essere informato dell'invito.
- _Altri membri del team_: vogliono raggiungere una dimensione utile per gli hackathon.

**Pre-condizioni.**

- L'attore è autenticato ed è **amministratore di un team** (`Appartenenza.amministratore = vero`). _(rimando: Autenticazione, §4.1)_
- Esiste l'utente da invitare.

**Post-condizioni (garanzia di successo).**

- Esiste un **invito in stato "pendente"** verso l'utente invitato, associato al team.
- L'utente invitato è stato **notificato** (Observer).
- _Oppure_ l'operazione è annullata e nessun invito viene creato.

**Scenario principale di successo.**

1. L'Amministratore richiede di invitare un utente al proprio team.
2. Il sistema presenta la ricerca/elenco degli utenti invitabili.
3. L'Amministratore seleziona l'utente e conferma l'invito.
4. Il sistema crea un **Invito** in stato "pendente" associato a (utente invitato, team).
5. Il sistema **notifica** l'utente invitato (Observer: il `ServizioNotifiche`/`Invito` — _Subject_ — notifica l'`Utente` registrato come _osservatore_).
6. Il sistema conferma l'invio dell'invito.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **3a. Esiste già un invito pendente** per quello stesso utente verso questo team:
  - 3a.1 Il sistema segnala il duplicato e non crea un secondo invito.
  - 3a.2 jump to 2.
- **3b. (cortesia, opzionale) L'utente selezionato appartiene già a un team:**
  - 3b.1 Il sistema avvisa che l'utente è già in un team (l'invito sarebbe comunque **non accettabile**: la verifica autoritativa è in UC10).
  - 3b.2 L'Amministratore sceglie un altro utente (jump to 2) o procede comunque.
- **\*a. In qualunque momento l'Amministratore annulla:** nessun invito creato.

**Regole di business esercitate.**

- **Solo l'Amministratore del team può invitare** (`Appartenenza.amministratore`).
- **Niente inviti pendenti duplicati** verso lo stesso utente per lo stesso team.
- La verifica "**un utente, un solo team**" è **autoritativa all'accettazione** (UC10), non qui (l'invito è solo una proposta).

**Requisiti speciali / note implementative.**

- Serve una **nuova entità** `Invito` (utente invitato, team, `stato {Pendente, Accettato, Rifiutato}`, dataInvito) — vedi §4.3. **`stato` è un semplice enum, NON un secondo pattern State**: il GoF State resta riservato all'Hackathon (per non diluire la narrativa dei pattern).
- **Observer**: alla creazione, il `Subject` notifica l'`Utente` invitato (Observer). Il meccanismo è **riusabile** per le future notifiche dei Mentori (proposte di call, segnalazioni — RELAZIONE §5.2). La semantica esatta (chi è Subject, quando l'Utente si registra) va fissata in progettazione — vedi §4.3.
- Auth non realizzata: l'amministratore corrente entra come parametro-stub.

**Frequenza d'uso.** Media (durante la formazione dei team).

**Problemi aperti.** Definire l'insieme degli "utenti invitabili" (tutti? esclusi quelli già in un team?). Invitare quando il team è già iscritto a un hackathon e l'aggiunta supererebbe `dimensioneMaxTeam`: la verifica è demandata a UC10 (estensione 5a). L'attore **Amministratore Team** non è citato in `progetto.MD` (che attribuisce l'invito all'`Utente` creatore): va **dichiarato nei requisiti** — la coerenza regge perché il creatore del team è amministratore (UC02) ed eventuali altri admin nascono da "Rende amministratore" (cfr. `iterazione1/legacy/REVISIONE_USE_CASE.md` §1.2).

**Suggerimento VP.** Modella l'**Observer** in un Sequence Diagram: `Subject` → `notifica()` → `Utente` (Observer). Crea Requirement "Solo l'amministratore invita".

---

### UC10 — Accetta Invito

| Campo                  | Valore            |
| ---------------------- | ----------------- |
| **Nome**               | Accetta Invito    |
| **ID**                 | UC10              |
| **Portata**            | HackHub (sistema) |
| **Livello**            | Obiettivo utente  |
| **Attore primario**    | Utente            |
| **Attori di supporto** | nessuno           |
| **Rank / Complessità** | Alta / Media      |

**Breve descrizione.** Un Utente con un invito pendente lo **accetta** e diventa **membro (non amministratore)** del team. Al momento dell'accettazione il sistema verifica **autoritativamente** l'invariante "un utente, un solo team" e la dimensione del team rispetto agli hackathon a cui è iscritto.

**Evento scatenante.** L'utente ha ricevuto un invito (UC09) e vuole unirsi al team.

**Parti interessate e interessi.**

- _Utente invitato_: vuole unirsi al team.
- _Amministratore / membri del team_: vogliono acquisire il nuovo membro.

**Pre-condizioni.**

- L'attore è autenticato. _(rimando: Autenticazione, §4.1)_
- Esiste un **invito pendente** verso di lui.

**Post-condizioni (garanzia di successo).**

- L'utente è **membro del team** (nuova `Appartenenza`, `amministratore = falso`).
- L'invito è nello stato **"accettato"**.
- _Oppure_ l'accettazione è rifiutata (invariante/dimensione) o l'invito non è più valido, e nulla cambia.

**Scenario principale di successo.**

1. L'Utente consulta i propri inviti pendenti.
2. L'Utente seleziona un invito e lo accetta.
3. Il sistema verifica che l'invito sia ancora **pendente e valido**.
4. Il sistema verifica che l'Utente **non appartenga già ad alcun team** (`RepoUtente.appartieneAdUnTeam`).
5. Il sistema verifica che l'aggiunta **non superi `dimensioneMaxTeam`** di **alcun** hackathon a cui il team è iscritto (vincolo più restrittivo).
6. Il sistema aggiunge l'Utente al team come membro (`Appartenenza`, `amministratore = falso`) e pone l'invito in stato "accettato".
7. Il sistema conferma l'adesione.

**Estensioni (flussi alternativi).** _(errori prima dell'happy path — §1.3)_

- **3a. L'invito non è più valido** (già accettato/rifiutato, oppure team disciolto):
  - 3a.1 Il sistema informa che l'invito non è più disponibile.
  - 3a.2 exit Scenario.
- **4a. L'Utente appartiene già a un team:**
  - 4a.1 Il sistema rifiuta l'accettazione e spiega l'invariante "**un solo team**".
  - 4a.2 exit Scenario.
- **5a. L'aggiunta supererebbe `dimensioneMaxTeam`** di un hackathon a cui il team è già iscritto:
  - 5a.1 Il sistema rifiuta l'accettazione e spiega il limite.
  - 5a.2 exit Scenario.
- **\*a. L'Utente rifiuta l'invito** (anziché accettarlo): l'invito passa a "rifiutato"; nessuna appartenenza creata. _(vedi Problemi aperti)_

**Regole di business esercitate.**

- **"Un utente, un solo team"** verificato **all'accettazione** (momento vincolante — chiude la finestra TOCTOU: invitato da libero, accetta dopo essere entrato altrove).
- La **crescita del team** non deve violare `dimensioneMaxTeam` di **alcun** hackathon a cui è iscritto (coerenza con il limite di UC03, iter. 1).
- Il **nuovo membro non è amministratore**.

**Requisiti speciali / note implementative.**

- Opera sull'entità `Invito` (UC09) e aggiunge un'`Appartenenza` al `Team`: il `Team` di iterazione 1 **non espone** un'operazione `aggiungiMembro(...)` (oggi i membri si impostano solo nel costruttore) → nuova operazione (§4.3).
- Gli **altri inviti pendenti** dell'utente **non** vengono toccati a cascata: restano pendenti ma diventano **non accettabili** per via dell'invariante (4a).

**Frequenza d'uso.** Media.

**Problemi aperti.**

- **Crescita del team dopo l'iscrizione**: alternativa al controllo del passo 5 → "**congelare**" i membri all'iscrizione (vietare aggiunte dopo che il team è iscritto). Decidere e documentare.
- **Scadenza/revoca** degli inviti: non gestita in questa iterazione.
- **"Rifiuta Invito"**: modellato come estensione `*a` di questo caso d'uso. _Nota:_ nel diagramma dei casi d'uso **non** esiste un "Rifiuta Invito" (c'è "Rifiuta Proposta Call", che riguarda le call dei Mentori via Calendar — flusso diverso); se lo si vuole esplicito va **aggiunto** come UC sotto l'Utente.

**Suggerimento VP.** Nel Sequence Diagram ordina gli operandi `alt` mettendo gli errori (3a/4a/5a) **prima** dell'happy path (convenzione `CLAUDE.md`). Crea Requirement "Invariante un-solo-team verificato all'accettazione".

---

## 3. Concatenamento end-to-end (coerenza pre/post-condizioni e dipendenze)

Questa iterazione **prosegue** la fetta verticale dell'iterazione 1 e ne salda i debiti. La post-condizione di un caso d'uso abilita la pre-condizione del successivo.

```
(iter. 1)  UC01 Crea Hackathon → UC02 Crea Team → UC03 Iscrive Team
           → UC04 Invia Sottomissione → UC05 Valuta Sottomissione

(iter. 2 — team reali, prerequisito non degenere di UC08)
   UC09 Invita Membro  →  UC10 Accetta Invito   ⇒  team multi-membro
        (Observer + Invito pendente)               (invariante un-solo-team)

(iter. 2 — ciclo di vita esplicito, pattern State)
   UC06 Avvia Hackathon          : in iscrizione → in corso     (now ≥ scadenzaIscrizioni)
        │  (le sottomissioni UC04/UC05 vivono in questa finestra)
   UC07 Inizia Fase Valutazione  : in corso → in valutazione     (now ≥ dataFine)
        │
   UC08 Proclama Vincitore       : in valutazione → concluso     (tutte valutate → registra vincitore)
```

**Dipendenza chiave (UC09/UC10 → UC08).** Con "un utente, un solo team" e senza inviti, ogni team avrebbe un solo membro: UC08 classificherebbe sottomissioni mono-fondatore. Realizzare UC09/UC10 nella stessa iterazione rende i team **multi-membro** e la dimostrazione di UC08 **non degenere**. Nello scenario di UC08, assumere che la sottomissione vincente provenga da un team con più membri (prodotto da UC09+UC10).

**Continuità con il pattern State.** UC04/UC05 (iter. 1) **leggevano** lo stato tramite le guard; UC06/UC07/UC08 (iter. 2) **fanno avanzare** lo stato tramite transizioni: insieme dimostrano il pattern GoF State per intero.

---

## 4. Appendice

### 4.1 Pre-condizione trasversale: Registrazione / Autenticazione

Come nell'iterazione 1, ogni caso d'uso autenticato presuppone **Registrazione** (attore Visitatore) e **Autenticazione**; restano modellati come **pre-condizione** (o «include» `Autenticazione`, cfr. `docs/UML_02_CasiUso.pdf` slide 22), non specificati in dettaglio. L'**utente corrente** (Organizzatore in UC06-UC08, Amministratore in UC09, Utente in UC10) entra ancora come **parametro-stub**: l'autenticazione reale è un debito noto, da affrontare se richiesto come strato di sicurezza (`docs/05_ProgettazioneArchitetturale.pdf`).

### 4.2 Contesto implementativo (vincoli di `progetto.MD`)

- Sviluppo in **Java**, poi **Spring Boot**; presentazione anche solo CLI e/o **API REST**.
- **Almeno due design pattern**: questa iterazione **completa lo State** (transizioni, UC06-UC08) e **introduce l'Observer** (notifica inviti, UC09) — così il requisito è coperto da **due pattern GoF nominati**, oltre ai GRASP.

### 4.3 Impatti sul modello di dominio e di progettazione (delta da portare nei file `*CHANGE`)

Le modifiche seguenti vanno nel **diagramma delle classi di progettazione** dell'iterazione 2 come file `*CHANGE` (non toccare i `*REALIZZATI`, regola `CLAUDE.md`):

- **`Stato`**: aggiungere le **operazioni di transizione** (es. `avvia()`, `iniziaValutazione()`, `concludi()`, oppure transizioni delegate che restituiscono il nuovo stato) — completano il GoF State, oggi guard-only.
- **`Hackathon`** (Context): aggiungere `vincitore: Team`; aggiungere i metodi `avvia()` / `iniziaFaseValutazione()` / `proclamaVincitore(team)` che **delegano allo stato corrente** la transizione.
- **`Invito`** (nuova entità): `utenteInvitato`, `team`, `stato: <enum> {Pendente, Accettato, Rifiutato}`, `dataInvito`. **Non** è un pattern State (enum semplice).
- **`Team`**: operazione `aggiungiMembro(utente)` che crea un'`Appartenenza` non amministratore (oggi i membri si impostano solo nel costruttore).
- **Observer**: introdurre `Subject` e `Observer`. Opzione consigliata: un `ServizioNotifiche` (Subject) osservato dagli `Utente` (Observer), così la registrazione observer↔subject è ben definita e il meccanismo è riusabile per i Mentori (futuro). Da fissare nel class/sequence diagram **chi** si registra e **quando** (l'invitato alla creazione dell'invito; opzionalmente l'Amministratore osserva i cambi di stato dell'invito, es. accettazione).
- **Boundary/Control/Repository** coerenti con l'EBC di iter. 1, **uno per caso d'uso**: `HandlerAvviaHackathon`/`HandlerIniziaValutazione`/`HandlerProclamaVincitore`/`HandlerInvito`/`HandlerAccettaInvito` (boundary), `Service*` (control), `RepoInvito` (persistenza, Pure Fabrication).

### 4.4 Pattern per caso d'uso

| UC   | Pattern in evidenza                                  | Note                                                                                                     |
| ---- | ---------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| UC06 | **State** (transizione), GRASP Controller            | la transizione è delegata allo stato corrente                                                            |
| UC07 | **State** (transizione), GRASP Controller            | guardia temporale = dataFine                                                                             |
| UC08 | **State** (transizione), Information Expert          | Information Expert su "tutte valutate" e sul ranking; registra `vincitore` (nessuna nuova entità creata) |
| UC09 | **Observer** (notifica), Creator, Controller         | crea `Invito`, notifica l'invitato; **niente State**                                                     |
| UC10 | Information Expert (invariante), Creator, Controller | crea `Appartenenza`; verifica un-solo-team + dimensione; **niente State**                                |

> **Nota (copertura dei pattern).** UC09/UC10 **non** esercitano lo State (nessuna transizione di hackathon): lo State è dimostrato da UC06-UC08, mentre UC09/UC10 portano l'**Observer** e i GRASP. È bene che ogni UC dell'iterazione riusi pattern noti del progetto.

### 4.5 Problemi aperti consolidati (decisioni da confermare)

1. **Rimozione dello stub auto-by-date** (obbligatoria): degradare l'avanzamento automatico a precondizione delle transizioni esplicite (§1.5 change log).
2. **D1**: guardia temporale `scadenzaIscrizioni` vs `dataInizio` per UC06; mantenere o no la guardia (alternativa "manuale puro").
3. **D2**: parità su UC08 → scelta dell'Organizzatore (default) vs `dataOra` più antica (deterministico); conclusione di un hackathon **senza vincitore** (0 sottomissioni).
4. **Avvio/inizio con insieme vuoto**: avviare con 0 team (UC06 3c), iniziare valutazione con 0 sottomissioni (UC07 3c) — avviso o blocco.
5. **D3/D4**: crescita del team dopo l'iscrizione (verifica al passo UC10.5 vs "congelamento" dei membri); scadenza/revoca degli inviti; "Rifiuta Invito" come estensione o UC separato (oggi **non** nel diagramma).
6. **Auth reale** (Registrazione/Autenticazione/Logout): ancora stub.

### 4.6 Riferimenti

- `progetto.MD` — regole di dominio e attori (Organizzatore proclama un solo vincitore a valutazioni complete; un utente un solo team; inviti/accettazione; montepremi via Sistema Pagamento).
- `docs/02_Processo.pdf` — UP iterativo/incrementale, risk-driven, architecture-centric.
- `docs/UML_02_CasiUso.pdf` — formato dei casi d'uso (slide 11-15) ed esempio dettagliato (slide 26-30); autenticazione come pre-condizione/«include» (slide 22).
- `docs/Design Patterns.pptx.pdf` — pattern **State** (le transizioni ne sono il nucleo) e **Observer** (notifiche).
- `docs/PatternsGRASP.pdf` — Controller, Creator, Information Expert, Pure Fabrication, Polymorphism.
- `docs/05_ProgettazioneArchitetturale.pdf` — architettura a strati, strato di sicurezza.
- `iterazione1/legacy/ITERAZIONE_1_CASI_USO.md` — metodologia di selezione dei casi d'uso (UP, criteri Larman).
- `iterazione1/legacy/CASI_USO_DETTAGLIATI.md` — formato fully-dressed di riferimento e §1.4 (transizioni automatiche da rendere esplicite).
- `iterazione1/diagrammiSequenza/RELAZIONE_REALIZZAZIONE.md` — EBC, pattern realizzati; §5.2 (Observer candidato per inviti/notifiche).
- `iterazione1/analisi/CLASSI_ANALISI_REALIZZATI.puml`, `iterazione1/progetto/CLASSI_PROGETTO_CHANGE.puml` — modello da estendere (vincitore, Invito, transizioni, Observer).
