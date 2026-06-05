# Stato dei lavori — Iterazione 2 HackHub

> **Scopo di questo file.** Memoria di lavoro/handoff: registra in modo preciso _cosa è stato fatto_, _quali decisioni sono state prese_ e _cosa resta_, così da ricostruire il contesto rileggendolo. Non è un deliverable UML: è un documento di stato.
> **Aggiornato al:** 2026-06-05.
> **Repo:** Appoggio (`origin` = `git@github.com:rizzutomatteo/HackHub-Appoggio.git`). Account di default per i commit: **matteo**.

---

## 0. Convenzioni del progetto (da rispettare sempre)

- **File `*REALIZZATI`** = versione autorevole dell'utente: si **consultano**, non si toccano. Le proposte di Claude vanno in file **`*CHANGE`**; l'utente le **promuove** a `*REALIZZATI` (di solito rifinendo in Visual Paradigm). Cartelle **`legacy/`** = materiale vecchio, solo consultazione.
- **Diagrammi di analisi**: stile _domain model_ (UML_03 slide 13) → **niente operazioni**, tipi/visibilità omessi (solo gli attributi enum mostrano il tipo), relazioni reificate come **classi-associazione** (UML_04 slide 10), stereotipo `«entità»`, vincoli non grafici in **legenda**, molteplicità su **entrambe** le estremità, `!pragma layout elk`.
- **Diagrammi di sequenza**: partizionamento **EBC** `actor → boundary "PaginaXxx" → control "GestoreXxx" → entity → participant "RepoXxx" «pure fabrication»`; messaggi `->`/ritorni `-->`/`create entity … <<crea>>`; `hide footbox` + `skinparam shadowing false`/`sequenceMessageAlign center`/`maxMessageSize 260`; frammenti `alt`/`opt`/`loop` con **errori-prima, happy path per ultimo**; al Repository accede **solo** il control.
- **Render**: solo Docker, `./scripts/render-diagrams.sh` (ora copre **tutte** le `iterazione*`); produce PNG+SVG accanto ai `.puml`. Verifica visiva **obbligatoria**: un diagramma non è "fatto" finché il render non è pulito (nessun incrocio); archi lunghi inevitabili da cicli del modello → instradati sul margine senza incroci e rifiniti in VP (mai stravolgere il modello per la grafica — regola 7).
- **Git multi-account**: commit firmati via SSH con `git as <matteo|ale|mozzo>`; la chiave di matteo ha passphrase (verificare `ssh-add -l` prima di committare). Repo del codice indipendente: comando `codice` (solo `iterazione1/codice/`).

---

## 1. Punto di partenza: cosa ha lasciato l'iterazione 1

Dominio **HackHub** (Processo Unificato). L'iterazione 1 ha consegnato lo **scheletro eseguibile** (walking skeleton) end-to-end:

`UC01 Crea Hackathon → UC02 Crea Team → UC03 Iscrive Team → UC04 Invia Sottomissione → UC05 Valuta Sottomissione`

- **Pattern**: GoF **State** (ciclo di vita Hackathon) + **GRASP** (Controller, Creator, Information Expert, Pure Fabrication, Polymorphism).
- **Architettura**: EBC (Boundary→Control→Entity) + Repository.
- **Fatti chiave del modello realizzato (codice in `iterazione1/codice/`)** — verificati leggendo il codice:
  - **State è "guard-only"**: gli stati (`InIscrizione/InCorso/InValutazione/Concluso`) implementano **solo** `iscrizioneConsentita()/sottomissioneConsentita()/valutazioneConsentita()`. **Le transizioni NON esistono**: in iter.1 il ciclo avanzava "automaticamente per data" (stub). `Concluso` esiste già (tutte le guard a falso).
  - `Hackathon` ha le date (`scadenzaIscrizioni`, `dataInizio`, `dataFine`), `stato`, `staff`. **NON ha un campo `vincitore`.**
  - Un solo Giudice per hackathon ⇒ ogni `Sottomissione` ha **al più una** `Valutazione` (`punteggio` int 0..10); `Sottomissione.getValutazione()` è `null` finché non valutata.
  - `Team` = `nome` + `List<Appartenenza>`; il creatore è `Appartenenza(amministratore=true)`. **Nessun meccanismo di invito / nessun `aggiungiMembro`**: oggi un team contiene **solo** il creatore.
  - "Un utente, un solo team" verificato via `RepoUtente.appartieneAdUnTeam`. `RepoSottomissione.sottomissioniDi(hackathon)` esiste.
  - **Auth non realizzata**: l'utente corrente entra come **parametro-stub**.

---

## 2. Scope dell'iterazione 2 (deciso con analisi "council")

**Tema: chiudere il ciclo di vita dell'hackathon + rendere reali i team.**

| Tier                        | Casi d'uso                                                                                                                      | Perché                                                                                                                         |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| **1** (obbligatorio)        | **UC06 Avvia Hackathon, UC07 Inizia Fase Valutazione, UC08 Proclama Vincitore** (Organizzatore)                                 | Completano il pattern State (le **transizioni**, assenti in iter.1) e chiudono la catena del valore (vincitore + conclusione). |
| **2** (forte)               | **UC09 Invita Membro** (Amministratore Team), **UC10 Accetta Invito** (Utente)                                                  | Rendono i team **multi-membro** (flusso inviti rinviato da UC02) e introducono il 2º pattern GoF **Observer** (notifiche).     |
| **3** (opzionale, rinviato) | Paga Montepremi (→ Sistema Pagamento)                                                                                           | Prima integrazione esterna onesta (stub).                                                                                      |
| **Rinviato a iter.3**       | Mentoring (Richiede Supporto / Propone Call / Accetta-Rifiuta Call → Calendar), moderazione, viste read-only, admin, auth reale | Flussi secondari / 2ª integrazione esterna.                                                                                    |

**Dipendenza chiave**: con "un utente, un solo team" e senza inviti ogni team avrebbe un solo membro ⇒ UC08 proclamerebbe team mono-fondatore. Perciò **UC09/UC10 fanno parte della stessa iterazione e precedono logicamente** la dimostrazione di UC08.

---

## 3. I 5 casi d'uso (UC06–UC10)

| ID   | Nome                    | Attore              | Transizione/effetto                                | Pattern in evidenza             |
| ---- | ----------------------- | ------------------- | -------------------------------------------------- | ------------------------------- |
| UC06 | Avvia Hackathon         | Organizzatore       | `InIscrizione → InCorso`                           | **State** (transizione)         |
| UC07 | Inizia Fase Valutazione | Organizzatore       | `InCorso → InValutazione`                          | **State** (transizione)         |
| UC08 | Proclama Vincitore      | Organizzatore       | `InValutazione → Concluso` + `vincitore`           | **State** + Information Expert  |
| UC09 | Invita Membro           | Amministratore Team | crea `Invito` pendente + notifica                  | **Observer** + Creator          |
| UC10 | Accetta Invito          | Utente              | crea `Appartenenza` (non admin) + invito Accettato | Information Expert (invariante) |

Specifiche dettagliate (formato Larman fully-dressed): **`iterazione2/useCase/CASI_USO_DETTAGLIATI.md`**.

---

## 4. Decisioni di progettazione fissate (D1–D4 + Invito)

- **D1 — trigger del ciclo di vita**: **manuale con guardia temporale**. L'Organizzatore esegue la transizione (UC reale + transizione State reale), ammessa solo **a/dopo** la data prevista: Avvia `now ≥ scadenzaIscrizioni`, Inizia Valutazione `now ≥ dataFine` (preserva "sottomissioni entro la scadenza"). **Lo stub auto-by-date di iter.1 va RIMOSSO** (degradato a precondizione) — altrimenti due meccanismi per la stessa transizione (modello incoerente).
- **D2 — vincitore (UC08)**: **punteggio massimo**; **parità risolta dall'Organizzatore** (un solo vincitore); proclamazione solo a **valutazioni complete** (`getValutazione()!=null` per tutte). Richiede il **nuovo attributo `Hackathon.vincitore`**. (Alternativa deterministica per la parità: `dataOra` più antica — non scelta.)
- **D3 — invito (UC09)**: invita **solo l'Amministratore Team**; verifiche vincolanti **all'accettazione** (non all'invito); **Observer** notifica l'invitato; niente inviti pendenti duplicati.
- **D4 — accettazione (UC10)**: invariante "un utente, un solo team" verificata **all'accettazione** (chiude la finestra TOCTOU) **+** verifica dimensione vs `dimensioneMaxTeam`; nuovo membro **non amministratore**.
- **Invito = ENTITÀ normale** (decisione utente, 2026-06-05), **non** classe-associazione. Motivo **valido = di dominio**: l'Invito ha identità e ciclo di vita propri (`Pendente/Accettato/Rifiutato`) e ammette **storico** (più inviti per la stessa coppia nel tempo). ⚠️ Il motivo "render più pulito" che avevo scritto **non è valido** (regola 7) → vedi §8 punto pendente.

---

## 5. Artefatti prodotti (percorsi esatti e stato)

| Artefatto                       | Percorso                                                                                           | Render         | Git                      |
| ------------------------------- | -------------------------------------------------------------------------------------------------- | -------------- | ------------------------ |
| Specifiche casi d'uso UC06–UC10 | `iterazione2/useCase/CASI_USO_DETTAGLIATI.md`                                                      | —              | **committato** (f8c1445) |
| Diagramma classi di **analisi** | `iterazione2/analisi/CLASSI_ANALISI_CHANGE.puml` (+ .png/.svg)                                     | sì, verificato | **committato** (a677bf9) |
| Seq. UC06                       | `iterazione2/diagrammiSequenza/avviaHackathon/SEQ_AvviaHackathon.puml` (+ .png/.svg)               | sì, verificato | **NON committato**       |
| Seq. UC07                       | `iterazione2/diagrammiSequenza/iniziaFaseValutazione/SEQ_IniziaFaseValutazione.puml` (+ .png/.svg) | sì, verificato | **NON committato**       |
| Seq. UC08                       | `iterazione2/diagrammiSequenza/proclamaVincitore/SEQ_ProclamaVincitore.puml` (+ .png/.svg)         | sì, verificato | **NON committato**       |
| Seq. UC09                       | `iterazione2/diagrammiSequenza/invitaMembro/SEQ_InvitaMembro.puml` (+ .png/.svg)                   | sì, verificato | **NON committato**       |
| Seq. UC10                       | `iterazione2/diagrammiSequenza/accettaInvito/SEQ_AccettaInvito.puml` (+ .png/.svg)                 | sì, verificato | **NON committato**       |

> I 5 diagrammi di sequenza sono **bozze `*CHANGE`** (cartelle senza suffisso `_REALIZZATI`): da promuovere/rifinire in Visual Paradigm. Sono stati renderizzati con `docker run plantuml/plantuml` e **ispezionati visivamente uno per uno** (layout pulito, attivazioni corrette, nessun incrocio) + validati con `-checkonly`.

---

## 6. Delta sul modello

### 6.1 Analisi (FATTO — in `CLASSI_ANALISI_CHANGE.puml`)

Soprainsieme puro del modello iter.1, con **solo** questi concetti di dominio aggiunti:

- **`vincitore`**: associazione `Hackathon "0..*" -- "0..1" Team : vincitore`.
- **`Invito`** (entità): `dataInvito`, `stato : StatoInvito`; associazioni `Invito "0..*" -- "1" Utente : invitato` e `Invito "0..*" -- "1" Team : verso`.
- **enum `StatoInvito`** `{Pendente, Accettato, Rifiutato}`; dipendenza `Invito ..> StatoInvito`.
- UC06/UC07 **non** aggiungono struttura (sono comportamento/transizioni).

### 6.2 Progettazione (DA FARE — elencato in `CASI_USO_DETTAGLIATI.md` §4.3; NON esiste ancora un class diagram di progetto per iter.2)

- **`Stato`**: aggiungere le **operazioni di transizione** `avvia()`, `iniziaValutazione()`, `concludi()` (creano/restituiscono lo stato successivo) — completano il GoF State.
- **`Hackathon`** (Context): aggiungere `vincitore: Team` e i metodi `avvia()` / `iniziaFaseValutazione()` / `proclamaVincitore(team)` che delegano allo stato corrente; guardie temporali `scadenzaIscrizioniRaggiunta()` / `dataFineRaggiunta()`.
- **`Invito`** (nuova entità) + enum `StatoInvito` (stato = enum semplice, **non** un secondo pattern State).
- **`Team`**: operazione `aggiungiMembro(utente)` (crea `Appartenenza` non admin).
- **Observer**: `ServizioNotifiche` (Subject) + `Utente` (Observer) con `notifica()`/`aggiorna()`.
- **EBC/persistenza**: `Handler*` (boundary) / `Service*` (control) / `RepoInvito` (Pure Fabrication). Mappa con i diagrammi di sequenza: `PaginaX`=`HandlerX`, `GestoreX`=`ServiceX`.

---

## 7. Cosa mostra ciascun diagramma di sequenza

Controller usati: **`GestoreHackathon`** (UC06/07/08), **`GestoreInviti`** (UC09/10). Boundary per-UC (`PaginaAvviaHackathon`, …).

- **UC06 / UC07** — _transizione State_: `Gestore→Hackathon.avvia()/iniziaFaseValutazione()` → guardia temporale (self-check Information Expert) → delega allo stato corrente che **`«crea»` e restituisce lo stato successivo** (`InCorso`/`InValutazione`) → `Hackathon` aggiorna `stato` → `salva`. `alt` errori-prima: 3a (data non raggiunta), 3b (stato errato, per polimorfismo), poi happy.
- **UC08** — `valutazioneConsentita()` (State) → `RepoSottomissione.sottomissioniDi` → `tutteValutate` (Information Expert) → `classificaPerPunteggio` → l'Organizzatore proclama → `Hackathon.proclamaVincitore(team)` setta `vincitore` e delega `concludi()` (`«crea» Concluso`). `alt` annidati: 1a (stato≠InValutazione), 3a (0 sottomissioni), 2a (non tutte valutate), poi happy; nota 4a (parità → Organizzatore).
- **UC09** — _Observer_: `GestoreInviti.invita` → `esisteInvitoPendente` → `«crea» inv:Invito (Pendente)` → `salva` → `ServizioNotifiche.notifica(inv)` → `Utente.aggiorna(inv)`. `alt`: 3a (duplicato) / happy; 3b (già in team) in nota.
- **UC10** — invariante: `accetta(invito)` → `inv.stato()` → `RepoUtente.appartieneAdUnTeam` → `Team.dimensioneCompatibile()` → `Team.aggiungiMembro` (`«crea» Appartenenza non admin`) → `salva(t)` + `inv.accetta()` + `salva(inv)`. `alt` errori-prima: 3a (invito non valido), 4a (già in team), 5a (oltre dimensione), poi happy.

---

## 8. Problemi aperti / decisioni pendenti

1. **Stub auto-by-date di iter.1**: da rimuovere/degradare a precondizione quando si tocca il codice/State (vedi D1). _(obbligatorio)_
2. **D1 dettaglio**: trigger di UC06 ancorato a `scadenzaIscrizioni` (scelta attuale, coerente con iter.1 §1.4) vs `dataInizio`.
3. **D2 dettaglio**: parità UC08 → scelta dell'Organizzatore (default) vs `dataOra` più antica (deterministico); conclusione **senza vincitore** (0 sottomissioni) non specificata.
4. **D4 / UC10 `dimensioneCompatibile()`**: assegnata al `Team`, ma nel codice iter.1 il Team non conosce le sue `Iscrizioni` (legame mediato dai Repo). Dipende dalla scelta D4: _verifica al volo_ (→ spostare il check su `GestoreInviti` via `RepoIscrizione`) **oppure** _"congelamento"_ dei membri all'iscrizione (→ il check in UC10 sparisce). **Da decidere.**
5. **UC08 `proclamaVincitore(team)`** non ri-valida che il team scelto abbia il punteggio massimo (si fida della selezione dalla classifica). Difendibile; eventualmente aggiungere validazione.
6. **UC09 Observer**: mostrata solo `notifica→aggiorna`, non la _sottoscrizione_ (`attach`) dell'Utente (avviene altrove). Accettabile.
7. **Auth reale** (Registrazione/Autenticazione/Logout): ancora stub (utente corrente = parametro).
8. **PENDENTE (micro)**: ripulire il commento in testa a `CLASSI_ANALISI_CHANGE.puml` togliendo la motivazione _"evita la 2ª classe-associazione / layout"_ per `Invito` (non valida, regola 7), lasciando solo la motivazione di dominio. _(in attesa di ok utente)_

---

## 9. Stato Git (Appoggio, branch `main`)

**Committato e pushato su `origin/main`** (autore: matteo, firmati `Verified`):

```
c18167d chore(scripts): render-diagrams.sh copre tutte le iterazioni (iterazione*)
a677bf9 docs(iterazione2): diagramma classi di analisi (CHANGE) — vincitore + Invito
c2dda85 chore: ignora i file di backup/lock di Visual Paradigm
5556c5a chore(codice): LICENSE (MIT), README e .gitignore del repo codice
baee84d feat(codice): assegna l'organizzatore a creaHackathon e avvia creaTeam con l'utente
49ebc87 docs(iterazione1): diagramma classi di progetto (CHANGE)
f8c1445 docs(iterazione2): specifiche dettagliate casi d'uso UC06-UC10
```

**NON ancora committato** (working tree, untracked): `iterazione2/diagrammiSequenza/` (i 5 diagrammi di sequenza + render) e **questo file** `iterazione2/STATO_ITERAZIONE2.md`.

**In sospeso (separato)**: push del codice (`feat(codice)` + LICENSE/README) sul **repo Codice indipendente** via `codice push` — il remote ufficiale potrebbe non essere ancora collegato.

---

## 10. Prossimi passi (ordine suggerito)

1. (Se richiesto) committare/pushare i 5 diagrammi di sequenza + questo file.
2. Decidere i problemi aperti §8 (almeno D4 punto 4, perché impatta UC10).
3. **Class diagram di progettazione iter.2** (`iterazione2/progetto/CLASSI_PROGETTO_CHANGE.puml`): applicare i delta §6.2 (transizioni State, `vincitore`, `Invito`, `aggiungiMembro`, Observer) partendo da `iterazione1/progetto/CLASSI_PROGETTO_CHANGE.puml`.
4. (Opzionale) `iterazione2/diagrammiSequenza/RELAZIONE_REALIZZAZIONE.md` come quella di iter.1 (motiva le scelte ancorandole ai materiali del corso).
5. Eventuale **diagramma di macchina a stati** dell'Hackathon (ora che le transizioni sono esplicite).
6. Implementazione codice (rimozione stub auto-by-date; transizioni; `vincitore`; `Invito`/`RepoInvito`; `Team.aggiungiMembro`; Observer) + test.

---

## 11. Riferimenti

- **Iterazione 2**: `useCase/CASI_USO_DETTAGLIATI.md`, `analisi/CLASSI_ANALISI_CHANGE.puml`, `diagrammiSequenza/*/SEQ_*.puml`.
- **Iterazione 1 (riferimento)**: `useCase/CASI_USO_REALIZZATI.md`, `analisi/CLASSI_ANALISI_REALIZZATI.puml`, `progetto/CLASSI_PROGETTO_REALIZZATI.puml` + `CLASSI_PROGETTO_CHANGE.puml`, `diagrammiSequenza/RELAZIONE_REALIZZAZIONE.md` + `*/SEQ_*.puml`; rationale in `legacy/ITERAZIONE_1_CASI_USO.md`, `legacy/CASI_USO_DETTAGLIATI.md`, `legacy/REVISIONE_USE_CASE.md`.
- **Codice realizzato**: `iterazione1/codice/{entity,state,boundary,service,repository}/`.
- **Materiali corso** (`docs/`): `02_Processo.pdf` (UP), `UML_02_CasiUso.pdf` (casi d'uso), `UML_03_Class.pdf`/`UML_04_Relazioni.pdf` (classi/relazioni), `UML_05_Interazioni.pdf` (sequenza), `UML_07_Progettazione.pdf`, `PatternsGRASP.pdf`, `Design Patterns.pptx.pdf` (State, Observer), `05_ProgettazioneArchitetturale.pdf`.
- **Traccia**: `progetto.MD` (root). **Istruzioni progetto**: `CLAUDE.md`, `git-multi-account.md`, `commit-spalmati-howto.md`, `codice-repo-howto.md`.
