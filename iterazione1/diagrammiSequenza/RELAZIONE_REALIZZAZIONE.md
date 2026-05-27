## Realizzazione dei casi d'uso — Diagrammi di sequenza (1ª iterazione HackHub)

Questo documento spiega **le scelte fatte** nella realizzazione dei 5 casi d'uso della prima iterazione
tramite i **diagrammi di sequenza** e ne dà le **motivazioni**, ancorandole ai materiali del corso
(`docs/`). Non è una specifica nuova: i casi d'uso sono in `iterazione1/useCase/CASI_USO_REALIZZATI.md`,
il modello di dominio in `iterazione1/analisi/CLASSI_ANALISI_REALIZZATI.puml`, e i diagrammi sorgente in
`iterazione1/diagrammiSequenza/*/SEQ_*.puml` (con i PNG/SVG renderizzati accanto). Qui si argomenta
**perché** quei diagrammi sono fatti così.

---

## 1. Scopo e collocazione nel Processo Unificato

Nel Processo Unificato la **realizzazione di un caso d'uso** (_use-case realization_) è il passo che
collega l'analisi alla progettazione: si parte dal **modello di analisi** (le classi di dominio) e si
mostra **come gli oggetti collaborano** per eseguire lo scenario del caso d'uso, attraverso uno o più
**diagrammi di interazione** (`docs/UML_05_Interazioni.pdf`). Il diagramma di sequenza è quindi la
"prova" dinamica che il modello statico di analisi è sufficiente a soddisfare il comportamento richiesto.

In questo passaggio compaiono **classi che non sono nel dominio del problema** ma nel **dominio della
soluzione**: secondo `docs/UML_07_Progettazione.pdf` (slide 4-6) le classi di progettazione nascono sia
dal dominio del problema (concetti dei requisiti) sia dal dominio della soluzione — _middleware,
framework, **design pattern**, librerie di utilità_. È esattamente ciò che facciamo qui introducendo i
**controller** EBC, i **Repository** (Pure Fabrication) e gli **oggetti-stato** del pattern State: non
sono entità di dominio, sono raffinamenti di progettazione.

> Lo stile delle classi di analisi resta quello _domain model_ (niente operazioni, tipi e visibilità
> omessi — `docs/UML_03_Class.pdf` slide 13); le responsabilità (i metodi) emergono **qui**, assegnandole
> con i criteri GRASP.

---

## 2. Le scelte dell'iterazione (recap)

I 5 casi d'uso scelti formano la **catena del valore** (_walking skeleton_) della piattaforma: un
percorso eseguibile end-to-end che attraversa tutti gli strati architetturali e coinvolge tutti gli
attori primari. La motivazione completa della selezione (ranking, copertura architetturale, rischio) è in
`iterazione1/legacy/ITERAZIONE_1_CASI_USO.md`; qui basti il percorso:

```
Crea Hackathon ─▶ Crea Team ─▶ Iscrivi Team ─▶ Invia Sottomissione ─▶ Valuta Sottomissione
(InIscrizione)               (Team→Hackathon)   (entro la finestra)    (giudizio + voto 0–10)
```

| UC   | Caso d'uso           | Attore primario | Diagramma di sequenza                              |
| ---- | -------------------- | --------------- | -------------------------------------------------- |
| UC01 | Crea Hackathon       | Organizzatore   | `creaHackathon/SEQ_CreaHackathon.puml`             |
| UC02 | Crea Team            | Utente          | `creaTeam/SEQ_CreaTeam.puml`                       |
| UC03 | Iscrivi Team         | Membro del team | `iscriviTeam/SEQ_IscriviTeam.puml`                 |
| UC04 | Invia Sottomissione  | Membro del team | `inviaSottomissione/SEQ_InviaSottomissione.puml`   |
| UC05 | Valuta Sottomissione | Giudice         | `valutaSottomissione/SEQ_ValutaSottomissione.puml` |

---

## 3. Notazione adottata (UML_05)

Tutti i diagrammi usano la notazione UML 2 per le interazioni (`docs/UML_05_Interazioni.pdf`):

- **Linee di vita** per attore, boundary, control, entità e Repository.
- **Barra di attivazione (focus)**: indica quando un oggetto è attivo nell'eseguire un'operazione.
- **Messaggio sincrono** `->` (punta piena): chiamata con attesa della risposta.
- **Messaggio di ritorno** `-->` (tratteggiato): risposta al chiamante.
- **Creazione** `«crea»` con `create entity`: nascita di un nuovo oggetto sulla sua linea di vita.
- **Frammenti combinati** `alt` / `opt` / `loop`: usati per mappare le **estensioni** dei casi d'uso
  (i flussi alternativi e d'errore) e le ripetizioni.
- **Niente tipi dei parametri** nei messaggi (`UML_05` slide 2): in fase di realizzazione interessano le
  responsabilità e le collaborazioni, non le firme complete (che si fissano in implementazione).

Scelte di leggibilità (coerenti con `CLAUDE.md` punto 5): `hide footbox` per eliminare il rumore in fondo
e alcuni `skinparam` (`maxMessageSize`, allineamento) per evitare scritte sovrapposte. Ogni `.puml` ha una
**legenda** che raccoglie i vincoli non esprimibili graficamente, così il diagramma resta pulito.

---

## 4. Il partizionamento EBC (Boundary–Control–Entity)

Ogni diagramma è partizionato secondo la tripletta **EBC** (Entity–Boundary–Control):

- **«boundary» `:PaginaXxx`** — il confine col mondo esterno (la schermata/endpoint con cui interagisce
  l'attore). Disaccoppia l'attore dalla logica.
- **«control» `:GestoreXxx`** — il **Controller** del caso d'uso. In `docs/PatternsGRASP.pdf` (slide 11) il
  Controller è descritto come _"il C dell'approccio BCE all'identificazione delle classi"_: un oggetto
  _use-case handler_ che riceve l'evento di sistema dalla boundary, orchestra le entità e coordina la
  persistenza. Uno per caso d'uso (`GestoreHackathon`, `GestoreTeam`, `GestoreIscrizioni`,
  `GestoreSottomissioni`, `GestoreValutazioni`).
- **«entity»** — le classi di **dominio** (Hackathon, Team, Iscrizione, Sottomissione, …): mantengono i
  dati e gli invarianti, **non** parlano con l'interfaccia né con la persistenza.

Accanto alla tripletta vivono i **Repository** (`RepositoryHackathon`, `RepositoryTeam`, …), marcati
`<<Pure Fabrication>>`: sono il layer di **persistenza**, esterno all'EBC.

> **Regola di accoppiamento (correzione applicata in questa iterazione).** Ai Repository accede **solo il
> Controller**: le entità di dominio non conoscono la persistenza. Nei diagrammi questo si vede dal fatto
> che l'entità _crea_ o _aggiorna_ l'oggetto e lo **restituisce** al controller, che poi invoca
> `salva(...)`. Così si rispetta **Low Coupling / High Cohesion** (`PatternsGRASP.pdf` slide 8-10): le
> entità restano coese sul dominio, la persistenza è isolata.

---

## 5. I pattern adottati e le motivazioni

Il progetto richiede **almeno due design pattern** (`progetto.MD`). Qui se ne usano due famiglie
complementari: il **pattern GoF State** per il ciclo di vita dell'Hackathon e l'insieme dei **pattern
GRASP** per l'assegnazione delle responsabilità.

### 5.1 GRASP (`docs/PatternsGRASP.pdf`)

- **Controller** (slide 11) — il `:GestoreXxx` riceve l'operazione di sistema dalla boundary e orchestra.
  _Perché:_ tiene la logica di coordinamento fuori dalla UI e fuori dalle entità (un solo punto di
  ingresso per scenario).
- **Creator** (slide 6) — assegna la creazione di A a chi _contiene/aggrega/registra/usa/inizializza_ A.
  _Dove:_ l'Hackathon crea i propri `Incarico` e lo `Stato` iniziale; il Team crea l'`Appartenenza`;
  l'Iscrizione crea la Sottomissione; la Sottomissione "riceve" la Valutazione. _Perché:_ il creatore è
  proprio l'oggetto che già possiede o aggrega il creato (composizioni del modello di analisi).
- **Information Expert** (slide 7) — la responsabilità va a chi ha l'informazione per assolverla.
  _Dove:_ l'Hackathon conosce `stato` e `dimensioneMaxTeam` (quindi decide l'iscrivibilità); la
  Valutazione conosce i propri attributi (quindi garantisce l'invariante `punteggio 0..10` e giudizio non
  vuoto). _Perché:_ riduce il _representational gap_ e l'accoppiamento.
- **Pure Fabrication** (slide 12) — i **Repository** sono classi "inventate" (non di dominio) a cui si
  affida la persistenza. _Perché:_ mettere il salvataggio dentro le entità ne abbasserebbe la coesione e
  violerebbe la separazione dominio/persistenza; il Repository la incapsula.
- **Polymorphism** (slide 13) — fa da ponte verso il pattern State: le guardie di stato sono operazioni
  polimorfe sull'oggetto-stato corrente, non `if`/`switch` sul tipo.

### 5.2 State (GoF) (`docs/Design Patterns.pptx.pdf`, pattern comportamentali)

Nel modello di analisi lo `Stato` dell'Hackathon è un **enum** (`InIscrizione`, `InCorso`,
`InValutazione`, `Concluso`). In progettazione lo si **raffina nel pattern State**: l'`Hackathon` è il
_context_ che mantiene un riferimento a un **oggetto-stato polimorfo** e gli **delega** le decisioni che
dipendono dallo stato:

- `iscrizioneConsentita()` → vera solo in `InIscrizione` (UC03);
- `sottomissioneConsentita()` → vera in `InCorso`, falsa in `InValutazione` (UC04);
- `valutazioneConsentita()` → vera in `InValutazione` (UC05).

_Motivazione (dalle slide del pattern):_ lo State si usa quando "_la classe include molto comportamento
dentro a case statement_" che dipendono dallo stato; sostituendoli con oggetti polimorfi si ottengono
**Single Responsibility** (un comportamento per stato) e **Open/Closed** (si aggiungono stati senza
toccare i client). Nei diagrammi questo significa che le estensioni del tipo _"stato sbagliato"_
(iscrizioni chiuse UC03 4.a, finestra chiusa UC04 3.a) sono realizzate **per polimorfismo**: è lo stato
corrente a rispondere "no", non un controllo di tipo nel controller.

> **Due pattern, requisito soddisfatto.** State (GoF) + GRASP coprono il vincolo di `progetto.MD`. Restano
> **candidati per le iterazioni successive**: **Observer** per le notifiche (gli inviti di Crea Team — feature futura —, le
> proposte/segnalazioni dei Mentori) e **Strategy** dove servisse variare un algoritmo (es. criteri di
> valutazione). Si introdurranno _dove calzano_, non in anticipo.

---

## 6. Realizzazione caso per caso

Per ogni caso d'uso: i partecipanti (linee di vita), il flusso essenziale, le responsabilità in gioco e
la **mappatura delle estensioni** del caso d'uso sui frammenti del diagramma.

### UC01 — Crea Hackathon · `creaHackathon/SEQ_CreaHackathon.puml`

**Partecipanti:** `Organizzatore`, `:PaginaCreaHackathon`, `:GestoreHackathon`, `:RepositoryHackathon`
(Pure Fabrication), `h:Hackathon`, `s:InIscrizione`, `incO/incG/incM:Incarico`.

**Flusso e responsabilità:** la boundary inoltra `creaHackathon(dati, giudice, mentori)` al controller.
Dentro l'`alt [dati validi AND esattamente 1 Giudice AND almeno 1 Mentore]`:

- **Creator + State:** `«crea» h:Hackathon`, che a sua volta `«crea» s:InIscrizione` — lo **stato iniziale
  è un invariante** del nuovo Hackathon (State GoF).
- **Creator:** `assegnaStaff(...)` fa creare all'Hackathon **un `Incarico` per ruolo** — `incO`
  (Organizzatore), `incG` (Giudice) e, in un **`loop`**, un `incM` per ciascun Mentore (1..\*). Questo
  rende esplicito il vincolo del modello: _per ogni Hackathon 1 Organizzatore, 1 Giudice, 1..\* Mentori_.
- **Pure Fabrication:** è il controller a chiamare `RepositoryHackathon.salva(h)`.

**Estensioni → frammenti:** il ramo `else` dell'`alt` realizza UC 4.a (vincolo staff) e UC 6.a (dati non
validi), che riportano al form. L'annullamento (#.a) è annotato in nota (nessun hackathon creato).

### UC02 — Crea Team · `creaTeam/SEQ_CreaTeam.puml`

**Partecipanti:** `Utente`, `:PaginaCreaTeam`, `:GestoreTeam`, `:RepositoryUtente`, `:RepositoryTeam`
(entrambi Pure Fabrication), `t:Team`, `:Appartenenza`.

**Flusso e responsabilità:**

- **Information Expert:** il controller verifica la precondizione _"un utente, un solo team"_ con
  `RepositoryUtente.appartieneAdUnTeam(utente)` — l'`alt` esterno separa il caso "non in team" da "già in
  un team".
- **Creator:** se il nome è valido e libero (`RepositoryTeam.esisteNome`), `«crea» t:Team`, che `«crea»`
  la propria `:Appartenenza` con `amministratore = vero` (il creatore è primo membro/amministratore —
  classe-associazione Utente–Team).
- **Inviti rinviati:** in questa iterazione il modulo chiede solo il **nome** del team; l'invito di altri
  utenti **non è realizzato** (feature futura, candidato **Observer** per le notifiche).

**Estensioni → frammenti:** `alt [nome valido e libero] / [mancante o già in uso]` realizza UC 4.a;
l'`else` esterno realizza la precondizione violata; annullamento (#.a) in nota.

> **Nessuno State qui:** il Team non ha un ciclo di vita a stati, quindi il pattern State non si applica —
> si usa solo dove serve (l'Hackathon).

### UC03 — Iscrivi Team · `iscriviTeam/SEQ_IscriviTeam.puml` _(State centrale)_

**Partecipanti:** `Membro del team`, `:PaginaIscrizione`, `:GestoreIscrizioni`, `:RepositoryIscrizione`
(Pure Fabrication), `h:Hackathon`, `s:InIscrizione`, `i:Iscrizione`.

**Flusso e responsabilità:**

- **Pure Fabrication (query):** il controller verifica il duplicato con
  `RepositoryIscrizione.esisteIscrizione(team, hackathon)`.
- **State + Information Expert:** chiede all'Hackathon `iscrivibile(team)`; l'Hackathon **delega allo
  stato** `iscrizioneConsentita()` (apertura) e confronta i membri con `dimensioneMaxTeam`.
- **Creator:** se tutto è verde, l'Hackathon `creaIscrizione(team)` → `«crea» i:Iscrizione`; il controller
  fa `RepositoryIscrizione.salva(i)`.

**Estensioni → frammenti:** un `alt` a **4 operandi** mappa esattamente le estensioni del caso d'uso:
iscrizione riuscita; **4.a** stato ≠ InIscrizione (iscrizioni chiuse, _per polimorfismo_); **4.b** team
già iscritto (nessun duplicato); **4.c** team oltre la dimensione massima.

### UC04 — Invia Sottomissione · `inviaSottomissione/SEQ_InviaSottomissione.puml` _(State centrale)_

**Partecipanti:** `Membro del team`, `:PaginaSottomissione`, `:GestoreSottomissioni`, `h:Hackathon`,
`s:InCorso`, `i:Iscrizione`, `:RepositorySottomissione` (Pure Fabrication), `sub:Sottomissione`.

**Flusso e responsabilità:**

- **State:** all'apertura del modulo, l'Hackathon delega allo stato `sottomissioneConsentita()`:
  `InCorso` ammette l'invio, `InValutazione` lo rifiuta (finestra chiusa).
- **Creator:** alla conferma, l'Iscrizione `creaSottomissione(dati)` → `«crea» sub:Sottomissione` con
  `dataOra = adesso` (timestamp del salvataggio). L'Iscrizione **contiene** la Sottomissione
  (composizione del modello).
- **Pure Fabrication:** l'Iscrizione restituisce `sub` al controller, che fa
  `RepositorySottomissione.salva(sub)`.

**Estensioni → frammenti:** `alt [InCorso] / [InValutazione]` realizza UC 3.a (finestra chiusa, per
polimorfismo); un `alt` **annidato** `[conferma] / [annulla]` realizza UC 5.a (ritorno al modulo).
L'annullamento generale (#.a) ripristina lo stato precedente (in nota).

### UC05 — Valuta Sottomissione · `valutaSottomissione/SEQ_ValutaSottomissione.puml` _(State + range 0..10)_

**Partecipanti:** `Giudice`, `:PaginaValutazione`, `:GestoreValutazioni`, `:RepositorySottomissione`
(Pure Fabrication), `h:Hackathon`, `s:InValutazione`, `sub:Sottomissione`, `v:Valutazione`,
`:RepositoryValutazione` (Pure Fabrication).

**Flusso e responsabilità:**

- **Pure Fabrication:** `RepositorySottomissione.sottomissioniDi(hackathon)` produce l'elenco (UC 2).
- **State:** alla selezione, l'Hackathon delega allo stato `valutazioneConsentita()` → abilitata solo in
  `InValutazione` (precondizione del caso d'uso).
- **Creator + Information Expert:** `sub.registraValutazione(...)`; se valido, la Sottomissione "riceve"
  la propria `«crea» v:Valutazione`. È la **Valutazione** a garantire l'**invariante** `punteggio 0..10` e
  giudizio non vuoto (Information Expert). Il controller fa `RepositoryValutazione.salva(v)`.

**Estensioni → frammenti:** un `opt [già valutata]` realizza UC 4.a (mostra la valutazione esistente,
modificabile — `registraValutazione` aggiorna invece di duplicare); un `alt` a **3 operandi** realizza la
valutazione valida, UC 7.a (punteggio fuori 0..10) e UC 7.b (giudizio vuoto), entrambi con ritorno ai
dettagli.

> **Vincolo non grafico (in legenda):** il giudice che valuta coincide con il **Giudice dell'Incarico**
> dell'Hackathon a cui appartiene l'Iscrizione della Sottomissione (modello di analisi).

---

## 7. Coerenza con il modello di analisi

I diagrammi riusano **gli stessi nomi** di `CLASSI_ANALISI_REALIZZATI.puml`: entità (`Utente`,
`Hackathon`, `Team`, `Incarico`, `Appartenenza`, `Iscrizione`, `Sottomissione`, `Valutazione`) e ruoli
(`contiene`, `riceve`, `redige`). In particolare:

- le **classi-associazione** `Incarico`, `Appartenenza`, `Iscrizione` compaiono come oggetti creati nei
  punti giusti (rispettivamente in UC01, UC02, UC03);
- le **composizioni** Iscrizione→Sottomissione e Sottomissione→Valutazione guidano il _Creator_ in UC04 e
  UC05;
- l'**enum `Stato`** è raffinato negli oggetti-stato del pattern State (UML_07 slide 5-6: classi dal
  dominio della soluzione);
- i **vincoli non esprimibili graficamente** (staff 1/1/1..\*, punteggio 0..10, giudice = Giudice
  dell'Incarico, un utente un solo team) sono ribaditi nelle **legende** dei diagrammi, non con note
  appese.

---

## 8. Tabella riassuntiva

| UC   | Attore        | Pattern in evidenza                                  | Estensioni mappate (frammenti)                   |
| ---- | ------------- | ---------------------------------------------------- | ------------------------------------------------ |
| UC01 | Organizzatore | Creator, State (stato iniziale), Pure Fabrication    | `alt` 4.a/6.a · `loop` Mentori · nota #.a        |
| UC02 | Utente        | Information Expert, Creator (no State)               | `alt` esterno (precond.) · `alt` 4.a             |
| UC03 | Membro team   | **State**, Information Expert, Creator, Pure Fabric. | `alt` 4 operandi (ok/4.a/4.b/4.c)                |
| UC04 | Membro team   | **State**, Creator (composizione), Pure Fabrication  | `alt` InCorso/InValutazione · `alt` annidato 5.a |
| UC05 | Giudice       | **State**, Creator, Information Expert (invariante)  | `opt` 4.a · `alt` 3 operandi (ok/7.a/7.b)        |

---

## 9. Riferimenti ai materiali del corso

- `docs/UML_05_Interazioni.pdf` — notazione dei diagrammi di interazione (linee di vita, focus, messaggi
  sincroni/ritorno, creazione, frammenti `alt/opt/loop`); **slide 2**: niente tipi dei parametri.
- `docs/UML_07_Progettazione.pdf` — **slide 4-6**: classi di progettazione dal _dominio della soluzione_
  (design pattern, framework); raffinamento del modello di analisi.
- `docs/PatternsGRASP.pdf` — Creator (**6**), Information Expert (**7**), Low Coupling/High Cohesion
  (**8-10**), Controller (**11**, "il C dell'approccio BCE"), Pure Fabrication (**12**), Polymorphism
  (**13**).
- `docs/Design Patterns.pptx.pdf` — pattern **State** (comportamentale): oggetti-stato polimorfi al posto
  dei _case statement_; SRP + Open/Closed.
- `docs/UML_03_Class.pdf` — **slide 13**: stile _domain model_ (niente operazioni in analisi).
- `progetto.MD` — requisito: _almeno due design pattern_; vincoli di dominio (punteggio 0..10, staff
  1 Giudice/1+ Mentori, un utente un solo team).
- `iterazione1/useCase/CASI_USO_REALIZZATI.md` — i 5 casi d'uso e le loro estensioni.
- `iterazione1/analisi/CLASSI_ANALISI_REALIZZATI.puml` — il modello di dominio realizzato.
- `iterazione1/legacy/ITERAZIONE_1_CASI_USO.md` — motivazione della selezione dei 5 casi d'uso.
