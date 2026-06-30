# Modifiche al diagramma delle classi di PROGETTO — da iter‑3 a iter‑4 (chiusura)

> **Scopo.** Checklist operativa di **tutto ciò che va aggiunto/cambiato** nel diagramma delle classi di progetto (Visual Paradigm) per portarlo dall'**iterazione 3** all'**iterazione 4** (iterazione di **chiusura**: erogazione del premio UC16, governance/segnalazioni UC17–UC18, accesso UC19–UC21, staff UC22).
> **Fonte.** `iterazione4/progetto/CLASSI_PROGETTO_CHANGE.puml` (vista **cumulativa** iter.1‑4). Derivati dai diagrammi di sequenza `iterazione4/diagrammiSequenza/*` (UC16–UC22) e da `iterazione4/useCase/CASI_USO_REALIZZATI.md §4.3–§4.4`.
> **Natura del delta.** È **tutto ADDITIVO**: nessuna classe/relazione/firma di iter‑3 va rimossa, rinominata o **ritipata**. A differenza di iter‑3 (che generalizzò il payload dell'Observer da `Invito` a `Notifica`), **iter‑4 non ha modifiche non‑additive**. iter‑4 è un **soprainsieme** di iter‑3.
> **Convenzioni.** Vedi `CONVENZIONI_DIAGRAMMI.md` (stereotipi «Handler»/«service»/«Entity»/«Repository»/«enumeration»/«interface»/«adapter»/«sistema esterno»; classi‑associazione mantenute; dipendenze «use»).
> **Naming dei control.** Qui i control «service» sono denominati **`Service*`** (come il deliverable iter‑3 `CLASSI_PROGETTO_REALIZZATI.jpg` e la fonte `CLASSI_PROGETTO_CHANGE.puml`). _Nei diagrammi di sequenza gli stessi control compaiono come `Gestore*` (`ServicePagamento`↔`GestorePagamento`, `ServiceSegnalazione`↔`GestoreSegnalazione`, `ServiceAccount`↔`GestoreAccount`, `ServiceNotifiche`↔`ServizioNotifiche`): split storico documentato in CONVENZIONI §2.3/§4._
> **Nota.** Getter **omessi per convenzione** (lo stato/gli attributi si leggono via l'attributo: nei sequenza i messaggi sono `stato()`, `vincitore()`, `montepremi()`). Gli `Incarico` sono parte dell'aggregato `Hackathon` (creati da `assegnaStaff`/`aggiungiMentore`) e si persistono via `RepoHackathon`: **non** esiste un `RepoIncarico`.

---

## 1. Classi NUOVE da creare (19)

### Boundary «Handler» (per‑UC) — 7

| Classe                                 | Operazioni                                                                                                                                                                            |
| -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **HandlerPagaMontepremi**              | `+ pagaMontepremi(hackathon : Hackathon) : void`                                                                                                                                      |
| **HandlerSegnalaTeam**                 | `+ segnalaTeam(team : Team, hackathon : Hackathon) : void`<br>`+ confermaSegnalazione(descrizione : string) : void`                                                                   |
| **HandlerGestisciSegnalazione**        | `+ visualizzaSegnalazioni(organizzatore : Utente) : void`<br>`+ gestisci(segnalazione : Segnalazione, decisione : string) : void`<br>`+ archivia(segnalazione : Segnalazione) : void` |
| **HandlerRegistrazione**               | `+ registrati() : void`<br>`+ inserisceDati(nome : string, email : string, password : string) : void`                                                                                 |
| **HandlerAutenticazione**              | `+ accedi() : void`<br>`+ inserisceCredenziali(email : string, password : string) : void`                                                                                             |
| **HandlerAccediInformazioniPubbliche** | `+ consultaHackathon() : void`                                                                                                                                                        |
| **HandlerAggiungiMentore**             | `+ aggiungiMentore(hackathon : Hackathon, utente : Utente) : void`                                                                                                                    |

### Control «service» — 3

| Classe                  | Operazioni                                                                                                                                                                                                                                                                                                                                                                                       |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **ServicePagamento**    | `+ paga(hackathon : Hackathon) : boolean`                                                                                                                                                                                                                                                                                                                                                        |
| **ServiceSegnalazione** | `+ segnala(mentore : Utente, team : Team, hackathon : Hackathon, descrizione : string) : boolean`<br>`+ segnalazioniDellOrganizzatore(organizzatore : Utente) : List<Segnalazione>`<br>`+ gestisci(segnalazione : Segnalazione, decisione : string) : boolean`<br>`+ archivia(segnalazione : Segnalazione) : boolean`<br>`- mentoreAssegnato(mentore : Utente, hackathon : Hackathon) : boolean` |
| **ServiceAccount**      | `+ registra(nome : string, email : string, password : string) : boolean`<br>`+ autentica(email : string, password : string) : Utente`<br>`- verificaCredenziali(utente : Utente, password : string) : boolean`                                                                                                                                                                                   |

> UC21 (Accedi a informazioni pubbliche) e UC22 (Aggiungi Mentore) **non** hanno un service nuovo: **riusano `ServiceHackathon`** (esteso, §2).

### Pattern ADAPTER (2° sistema esterno — Sistema di Pagamento; **riuso** della famiglia GoF di iter‑3) — 3

| Classe               | Stereotipo        | Ruolo GoF      | Operazioni                                            |
| -------------------- | ----------------- | -------------- | ----------------------------------------------------- |
| **PaymentGateway**   | «interface»       | Target (porta) | `+ erogaPremio(team : Team, importo : long) : string` |
| **AdapterPagamento** | «adapter»         | Adapter        | `+ erogaPremio(team : Team, importo : long) : string` |
| **SistemaPagamento** | «sistema esterno» | Adaptee        | `+ eroga(importo : long) : string`                    |

> I `service` dipendono **solo dalla porta** `PaymentGateway` (DIP); il Sistema di Pagamento è raggiunto **solo** via `AdapterPagamento` (contratto sincrono, useCase §1.5). `erogaPremio` restituisce il `riferimentoPagamento` in caso di successo, altrimenti segnala l'indisponibilità/il rifiuto. **Simmetrico a `CalendarGateway`/`AdapterCalendar`/`Calendar` (iter‑3):** a fine progetto i **due** sistemi esterni di `progetto.MD` sono isolati dalla **stessa** famiglia di pattern.

### Entity «Entity» — 2

| Classe           | Attributi                                                                                                         | Operazioni                                                       |
| ---------------- | ----------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------- |
| **Pagamento**    | `- importo : long`<br>`- dataPagamento : date`<br>`- riferimentoPagamento : string`<br>`- stato : StatoPagamento` | _(nessuna; creata e letta)_                                      |
| **Segnalazione** | `- descrizione : string`<br>`- dataSegnalazione : date`<br>`- stato : StatoSegnalazione`                          | `+ gestisci(decisione : string) : void`<br>`+ archivia() : void` |

### Enum «enumeration» — 2

| Classe                | Valori                             |
| --------------------- | ---------------------------------- |
| **StatoPagamento**    | `Inviato`, `Completato`, `Fallito` |
| **StatoSegnalazione** | `Aperta`, `Gestita`, `Archiviata`  |

> Gli stati sono **enum semplici, NON un pattern State** (decisione **D2**): il GoF State resta riservato all'`Hackathon`, coerente con `Invito`/`RichiestaSupporto`/`PropostaCall`.

### Repository «Repository» — 2

| Classe               | Operazioni                                                                                                                                                                                                                  |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **RepoPagamento**    | `+ esistePagamentoCompletato(hackathon : Hackathon) : boolean`<br>`+ salva(pag : Pagamento) : void`                                                                                                                         |
| **RepoSegnalazione** | `+ esisteSegnalazioneAperta(mentore : Utente, team : Team, hackathon : Hackathon) : boolean`<br>`+ trovaPerHackathonDellOrganizzatore(organizzatore : Utente) : List<Segnalazione>`<br>`+ salva(seg : Segnalazione) : void` |

---

## 2. Operazioni/attributi da AGGIUNGERE a classi ESISTENTI (5) — **tutte additive**

| Classe esistente     | Da aggiungere                                                                                                                                                                                                                                                                                        |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ServiceHackathon** | `+ hackathonPubblici() : List<Hackathon>` (UC21)<br>`+ aggiungiMentore(organizzatore : Utente, hackathon : Hackathon, utente : Utente) : boolean` (UC22)<br>`- organizzatoreAssegnato(organizzatore : Utente, hackathon : Hackathon) : boolean` (UC22 — scope via `Incarico`, ruolo = Organizzatore) |
| **Hackathon**        | `+ aggiungiMentore(utente : Utente) : Incarico` (UC22 — **riuso della logica di `assegnaStaff`**: crea un `Incarico` ruolo = Mentore)                                                                                                                                                                |
| **Utente**           | `- password : string` (attributo — credenziale, UC19/UC20)<br>`+ verificaPassword(password : string) : boolean` (UC20)                                                                                                                                                                               |
| **RepoUtente**       | `+ esisteEmail(email : string) : boolean` (UC19)<br>`+ trovaPerEmail(email : string) : Utente` (UC20)<br>`+ salva(u : Utente) : void` (UC19)                                                                                                                                                         |
| **RepoHackathon**    | `+ tutti() : List<Hackathon>` (UC21)<br>`+ salva(h : Hackathon) : void` (UC16/UC22) — nel deliverable iter‑3 era un box **senza metodi** (svista VP); iter‑4 lo rende esplicito perché ora è usato                                                                                                   |

> **Nessuna ritipatura, nessuna rimozione.** L'`Utente` realizzava già `Observer` da iter‑2 e `aggiorna(n : Notifica)` è già a payload `Notifica` da iter‑3: **non** si tocca. Lo **State** dell'`Hackathon` **non** cambia: iter‑4 lo **legge** soltanto (UC16 richiede "Concluso" con vincitore; UC22 richiede non‑"Concluso"), non aggiunge transizioni.

---

## 3. Relazioni NUOVE da disegnare (~31)

### Pattern Adapter (Sistema di Pagamento)

| Da → A                                  | Tipo                                             | Estremi (molteplicità)                      | Etichetta |
| --------------------------------------- | ------------------------------------------------ | ------------------------------------------- | --------- |
| **PaymentGateway ⊳⤏ AdapterPagamento**  | **realizzazione** (triangolo vuoto tratteggiato) | —                                           | —         |
| **AdapterPagamento → SistemaPagamento** | associazione **navigabile** (→)                  | AdapterPagamento `1` / SistemaPagamento `1` | `adaptee` |

### Pattern Observer (nuova realizzazione del marker)

| Da → A                       | Tipo          | Note                                                                                  |
| ---------------------------- | ------------- | ------------------------------------------------------------------------------------- |
| **Notifica ⊳⤏ Segnalazione** | realizzazione | `Segnalazione` è un nuovo **payload notificabile** (decisione **D6**: riuso Observer) |

> `Subject ⊳⤏ ServiceNotifiche`, `Observer ⊳⤏ Utente` e `Subject ◇— Observer : osservatori` **esistono già** (iter‑2), payload già `Notifica` (iter‑3): **non si ridisegnano**.

### Associazioni di Pagamento (UC16, navigabili)

| Da → A                    | Estremi (molteplicità)           | Etichetta      |
| ------------------------- | -------------------------------- | -------------- |
| **Pagamento → Hackathon** | Pagamento `0..*` / Hackathon `1` | `premio`       |
| **Pagamento → Team**      | Pagamento `0..*` / Team `1`      | `beneficiario` |

### Associazioni di Segnalazione (UC17/UC18, navigabili)

| Da → A                       | Estremi (molteplicità)              | Etichetta    |
| ---------------------------- | ----------------------------------- | ------------ |
| **Segnalazione → Utente**    | Segnalazione `0..*` / Utente `1`    | `segnalante` |
| **Segnalazione → Team**      | Segnalazione `0..*` / Team `1`      | `segnalato`  |
| **Segnalazione → Hackathon** | Segnalazione `0..*` / Hackathon `1` | `contesto`   |

### Dipendenze verso le enum

- `Pagamento ⤏ StatoPagamento : «use»`
- `Segnalazione ⤏ StatoSegnalazione : «use»`

### Dipendenze «use» EBC (Handler → service)

- `HandlerPagaMontepremi → ServicePagamento`
- `HandlerSegnalaTeam → ServiceSegnalazione`
- `HandlerGestisciSegnalazione → ServiceSegnalazione`
- `HandlerRegistrazione → ServiceAccount`
- `HandlerAutenticazione → ServiceAccount`
- `HandlerAccediInformazioniPubbliche → ServiceHackathon` ← **riuso** del service esistente (UC21)
- `HandlerAggiungiMentore → ServiceHackathon` ← **riuso** del service esistente (UC22)

### Dipendenze «use» service → Repository

- `ServicePagamento → RepoPagamento`
- `ServicePagamento → RepoHackathon` ← **uso nuovo** di un Repo esistente
- `ServiceSegnalazione → RepoSegnalazione`
- `ServiceAccount → RepoUtente` ← **uso nuovo** di un Repo esistente (`esisteEmail`/`trovaPerEmail`/`salva`)

### Dipendenze «use» service → Entity / Observer / Adapter

- `ServicePagamento → Pagamento`
- `ServicePagamento → Hackathon` ← entità esistente (vincitore/montepremi, guardia)
- `ServicePagamento → Team` ← entità esistente (beneficiario)
- `ServicePagamento → PaymentGateway`
- `ServiceSegnalazione → Segnalazione`
- `ServiceSegnalazione → Hackathon` ← entità esistente (contesto)
- `ServiceSegnalazione → Incarico` ← entità esistente (`mentoreAssegnato`, ruolo = Mentore)
- `ServiceSegnalazione → ServiceNotifiche` ← Observer (notifica l'Organizzatore)
- `ServiceAccount → Utente` ← entità esistente (crea/verifica)
- `ServiceHackathon → Incarico` ← **uso nuovo** di un'entità esistente (`organizzatoreAssegnato`, ruolo = Organizzatore)

---

## 4. Cosa NON toccare (resta identico a iter‑3)

- **Tutte** le classi di iter‑1/2/3: i 15 Handler precedenti, i service `ServiceTeam`/`ServiceHackathon`(esteso, §2)/`ServiceIscrizione`/`ServiceSottomissione`/`ServiceValutazione`/`ServiceInvito`/`ServiceSupporto`/`ServiceCall`/`ServiceNotifiche`, le entità (tranne `Utente` e `Hackathon`, estese in §2), i Repo (tranne `RepoUtente` e `RepoHackathon`, estesi in §2), gli enum `RuoloStaff`/`StatoInvito`/`StatoRichiesta`/`StatoCall`.
- Il **pattern State** dell'`Hackathon`: `abstract Stato` con guardie **e** transizioni (`avvia/iniziaValutazione/concludi`); `Stato <|-- InIscrizione/InCorso/InValutazione/Concluso`; attributo `Hackathon.stato : Stato` e associazione `Hackathon → Stato : stato corrente`. **In iter‑4 lo State non cambia** (D2/D3: viene solo letto).
- Il **pattern Observer** di iter‑2/3: interfacce `Subject`/`Observer`/`Notifica`, realizzazioni `Subject ⊳⤏ ServiceNotifiche` e `Observer ⊳⤏ Utente`, aggregazione `Subject ◇— Observer : osservatori`, e le realizzazioni `Notifica ⊳⤏ Invito/RichiestaSupporto/PropostaCall` (si **aggiunge solo** `Notifica ⊳⤏ Segnalazione`, §3).
- Il **pattern Adapter** di iter‑3: `CalendarGateway` «interface» / `AdapterCalendar` «adapter» / `Calendar` «sistema esterno» e `AdapterCalendar → Calendar : adaptee` (si **aggiunge** la famiglia gemella del pagamento, §1/§3).
- Le **classi‑associazione** `Incarico` / `Appartenenza` / `Iscrizione` (notazione classe‑associazione, NON reificate).
- La **catena del valore** (composizioni): `Iscrizione *— Sottomissione : contiene`, `Valutazione *— Sottomissione : riceve`; `Utente — Valutazione : redige`.
- Le associazioni di iter‑2/3: `Hackathon → Team : vincitore`, `Team — Invito : inviti`, `Invito → Utente : invitato`, e le associazioni del mentoring (`richiedente`/`contesto`/`destinatario`/`proponente`/`in risposta a`).

---

## 5. Riepilogo

- **19** classi nuove: **7 Handler** · **3 service** (`ServicePagamento`, `ServiceSegnalazione`, `ServiceAccount`) · **3 Adapter** (`PaymentGateway` «interface», `AdapterPagamento` «adapter», `SistemaPagamento` «sistema esterno») · **2 Entity** (`Pagamento`, `Segnalazione`) · **2 enum** (`StatoPagamento`, `StatoSegnalazione`) · **2 Repository** (`RepoPagamento`, `RepoSegnalazione`).
- **5** classi esistenti **estese (additive)**: `ServiceHackathon` (+`hackathonPubblici`, +`aggiungiMentore`, +`organizzatoreAssegnato`) · `Hackathon` (+`aggiungiMentore`) · `Utente` (+`password`, +`verificaPassword`) · `RepoUtente` (+`esisteEmail`/+`trovaPerEmail`/+`salva`) · `RepoHackathon` (+`tutti`/+`salva`).
- **~31** relazioni nuove: 2 Adapter · 1 realizzazione `Notifica ⊳⤏ Segnalazione` · 2 associazioni Pagamento · 3 associazioni Segnalazione · 2 dipendenze enum · 7 EBC «use» · 4 service→Repo «use» · 10 service→Entity/Observer/Adapter «use».
- **0** modifiche non‑additive · **0** ritipature · **0** rimozioni.

**Pattern in iter‑4:** **Adapter** _riusato_ (2° sistema esterno, Sistema di Pagamento) · **Observer** _riusato_ (nuovo payload `Segnalazione`) · **State** _invariato_ (solo letto). **Nessun pattern nuovo** — l'ultima iterazione **consolida**, non aggiunge. **GRASP** invariati (Controller, Creator, Information Expert, Pure Fabrication — di cui ora anche `PaymentGateway` —, Polymorphism).

### Decisioni rilevanti per il diagramma (rif. `CASI_USO_REALIZZATI.md §1.6`, council)

- **D1 — Secondo sistema esterno via Adapter.** L'erogazione del premio è delegata al Sistema di Pagamento tramite `PaymentGateway` (Adapter), **stessa famiglia** di `CalendarGateway`: coerenza architetturale, non un terzo pattern nuovo.
- **D2 — `StatoPagamento`/`StatoSegnalazione` = enum semplici, NON pattern State.**
- **D3 — Pre‑condizione di pagamento.** Si paga **solo** un hackathon **"Concluso"** con **vincitore** proclamato (UC08) e **non già pagato** (`RepoPagamento.esistePagamentoCompletato`, idempotenza: al più un `Pagamento` "Completato" per hackathon). Lo stato "Concluso" ha le guardie a falso ma **non** blocca il pagamento (azione diversa).
- **D4 — Autenticazione "sottile".** UC19/UC20 rendono reale l'"utente corrente" (finora parametro‑stub): si aggiunge `Utente.password` + `ServiceAccount`. **Fuori scope** (non nel diagramma): hashing robusto, sessioni/token, ruoli a runtime, logout.
- **D5 — Scope dell'Organizzatore via `Incarico`.** In UC18/UC22 l'Organizzatore vede/agisce **solo** sugli hackathon a cui è assegnato (`Incarico`, ruolo = Organizzatore) — stessa convenzione di scope dei Mentori (iter‑3).
- **D6 — Riuso Observer.** Le notifiche di segnalazione (UC17 → Organizzatore) riusano `ServiceNotifiche` (Subject)/`Observer`; `Segnalazione` realizza `Notifica`.

### Problemi aperti noti (non difetti del diagramma — useCase §4.5)

- `StatoPagamento.Inviato` è uno stato iniziale/transitorio; l'idempotenza guarda il "Completato". `StatoSegnalazione.Archiviata` è raggiunto da UC18 3b.
- **Osservatori opzionali** (non disegnati): notifica al team vincitore dell'avvenuto pagamento (UC16), notifica al nuovo Mentore (UC22) — consigliate per simmetria, non obbligatorie.
- **Destinatario tecnico del pagamento**: il `Team` non ha coordinate di pagamento nel modello (a chi è intestata l'erogazione — Amministratore?); valuta/decimali del `montepremi : long`; rimborsi/retry.
- **Azione disciplinare forte** (UC18): squalifica/esclusione impatterebbe UC08 e lo stato dell'iscrizione → tenuta **fuori scope** (la decisione resta un esito testuale su `Segnalazione`).
