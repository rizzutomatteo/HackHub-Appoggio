# Modifiche al diagramma delle classi di PROGETTO — da iter‑2 a iter‑3

> **Scopo.** Checklist operativa di **tutto ciò che va aggiunto/cambiato** nel diagramma delle classi di progetto (Visual Paradigm) per portarlo dall'**iterazione 2** all'**iterazione 3** (catena del mentoring, UC11–UC15).
> **Fonte.** `iterazione3/progetto/CLASSI_PROGETTO_CHANGE.puml` (vista **cumulativa** iter.1+2+3) e `CLASSI_PROGETTO_FOCUS_CHANGE.puml` (vista **focus** sul solo delta). Derivati dai diagrammi di sequenza UC11–UC15.
> **Natura del delta.** Quasi tutto **additivo** (nuove classi/relazioni). **UNICA eccezione non additiva:** la generalizzazione del payload dell'**Observer** da `Invito` a `Notifica` (ritipa 4 operazioni esistenti — vedi §2 e ⚠️ in fondo).
> **Convenzioni.** Vedi `CONVENZIONI_DIAGRAMMI.md` (stereotipi «Handler»/«service»/«Entity»/«Repository»/«enumeration»/«interface» + i nuovi «adapter»/«sistema esterno»; classi‑associazione mantenute; dipendenze «use»).
> **Nota.** Questo documento riflette il diagramma **dopo** la revisione (council): firme già corrette (rimosso `verificaDisponibilita`, aggiunto `RepoPropostaCall.propostePerTeam`, handler `HandlerRichiedeSupporto`, dipendenze `GestoreCall → Team/Incarico`). Lo **stato** di `PropostaCall` si legge via l'attributo `- stato` (getter **omesso per convenzione**, come `Invito`; nei diagrammi di sequenza il messaggio è `stato()`).

---

## 1. Classi NUOVE da creare (17)

### Boundary «Handler» (per‑UC)

| Classe                         | Operazioni                                                                                                          |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------------- |
| **HandlerRichiedeSupporto**    | `+ richiediSupporto(team : Team, hackathon : Hackathon) : void`<br>`+ confermaRichiesta(messaggio : string) : void` |
| **HandlerVisualizzaRichieste** | `+ visualizzaRichieste(mentore : Utente) : void`                                                                    |
| **HandlerProponeCall**         | `+ proponiCall(richiesta : RichiestaSupporto) : void`<br>`+ confermaSlot(slot : dateTime) : void`                   |
| **HandlerAccettaCall**         | `+ accettaProposta(proposta : PropostaCall) : void`                                                                 |
| **HandlerRifiutaCall**         | `+ rifiutaProposta(proposta : PropostaCall) : void`                                                                 |

### Control «service»

| Classe              | Operazioni                                                                                                                                                                                                                                                                                         |
| ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **GestoreSupporto** | `+ richiediSupporto(team : Team, hackathon : Hackathon, messaggio : string) : boolean`<br>`+ richiesteDelMentore(mentore : Utente) : List<RichiestaSupporto>`                                                                                                                                      |
| **GestoreCall**     | `+ proponiCall(mentore : Utente, richiesta : RichiestaSupporto, slot : dateTime) : boolean`<br>`+ accettaProposta(proposta : PropostaCall) : boolean`<br>`+ rifiutaProposta(proposta : PropostaCall) : boolean`<br>`- mentoreAssegnato(mentore : Utente, richiesta : RichiestaSupporto) : boolean` |

### Pattern ADAPTER (terza famiglia GoF — isola il sistema esterno Calendar)

| Classe              | Stereotipo        | Ruolo GoF      | Operazioni                                                                                               |
| ------------------- | ----------------- | -------------- | -------------------------------------------------------------------------------------------------------- |
| **CalendarGateway** | «interface»       | Target (porta) | `+ riservaSlot(slot : dateTime) : string`<br>`+ confermaPrenotazione(proposta : PropostaCall) : boolean` |
| **AdapterCalendar** | «adapter»         | Adapter        | `+ riservaSlot(slot : dateTime) : string`<br>`+ confermaPrenotazione(proposta : PropostaCall) : boolean` |
| **Calendar**        | «sistema esterno» | Adaptee        | `+ riserva(slot : dateTime) : string`<br>`+ conferma(riferimentoPrenotazione : string) : boolean`        |

> I `service` dipendono **solo dalla porta** `CalendarGateway` (DIP); il sistema esterno è raggiunto **solo** via `AdapterCalendar`. `riservaSlot` = prenotazione tentativa (UC13), `confermaPrenotazione` = conferma (UC14); UC15 **non** tocca Calendar.

### Interfaccia «interface» (abilita la generalizzazione dell'Observer)

| Classe       | Operazioni                                                                                                                                    |
| ------------ | --------------------------------------------------------------------------------------------------------------------------------------------- |
| **Notifica** | _(marker, nessuna operazione)_ — è il **payload comune** dell'Observer, realizzato da `Invito` (iter.2), `RichiestaSupporto`, `PropostaCall`. |

### Entity «Entity»

| Classe                | Attributi                                                                                       | Operazioni                                   |
| --------------------- | ----------------------------------------------------------------------------------------------- | -------------------------------------------- |
| **RichiestaSupporto** | `- messaggio : string`<br>`- dataRichiesta : date`<br>`- stato : StatoRichiesta`                | _(nessuna; creata e letta)_                  |
| **PropostaCall**      | `- dataOraProposta : dateTime`<br>`- riferimentoPrenotazione : string`<br>`- stato : StatoCall` | `+ accetta() : void`<br>`+ rifiuta() : void` |

### Enum «enumeration»

| Classe             | Valori                               |
| ------------------ | ------------------------------------ |
| **StatoRichiesta** | `Aperta`, `Gestita`, `Chiusa`        |
| **StatoCall**      | `Proposta`, `Accettata`, `Rifiutata` |

> Gli stati sono **enum semplici, NON un pattern State** (decisione D2): il GoF State resta riservato all'`Hackathon`.

### Repository «Repository»

| Classe                    | Operazioni                                                                                                                                                                                        |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **RepoRichiestaSupporto** | `+ esisteRichiestaAperta(team : Team, hackathon : Hackathon) : boolean`<br>`+ trovaPerHackathonDelMentore(mentore : Utente) : List<RichiestaSupporto>`<br>`+ salva(r : RichiestaSupporto) : void` |
| **RepoPropostaCall**      | `+ propostePerTeam(team : Team) : List<PropostaCall>`<br>`+ salva(proposta : PropostaCall) : void`                                                                                                |

---

## 2. Operazioni/attributi da MODIFICARE/AGGIUNGERE a classi ESISTENTI (6)

| Classe esistente         | Da modificare / aggiungere                                                                                                                                                                  |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **RepoIscrizione**       | **aggiungi** `+ iscrizioneInCorso(team : Team, hackathon : Hackathon) : boolean` (UC11). _(Se non già presente dal codice iter.2: anche `+ iscrizioniDi(team : Team) : List<Iscrizione>`.)_ |
| **Subject** (interface)  | ⚠️ **ritipa** `notifica(invito : Invito)` → `+ notifica(n : Notifica) : void`                                                                                                               |
| **Observer** (interface) | ⚠️ **ritipa** `aggiorna(invito : Invito)` → `+ aggiorna(n : Notifica) : void`                                                                                                               |
| **ServizioNotifiche**    | ⚠️ **ritipa** `notifica(invito : Invito)` → `+ notifica(n : Notifica) : void` (`registra`/`rimuovi` invariati)                                                                              |
| **Utente**               | ⚠️ **ritipa** `aggiorna(invito : Invito)` → `+ aggiorna(n : Notifica) : void`                                                                                                               |
| **Invito**               | nessun cambiamento ai membri, ma ora **realizza `Notifica`** (nuova realizzazione `Notifica <\|.. Invito`)                                                                                  |

> **Questa è l'unica modifica NON additiva di iter‑3.** L'Observer di iter‑2 trasportava il solo `Invito`; per riusarlo anche per `RichiestaSupporto` e `PropostaCall` il payload viene generalizzato al marker `Notifica`. Conseguenza sul **codice realizzato iter‑2**: `Observer.aggiorna(Invito)`/`Subject.notifica(Invito)`/`Utente.aggiorna(Invito)`/`ServizioNotifiche.notifica(Invito)` andranno allineati a `Notifica` se si promuove il diagramma.

---

## 3. Relazioni NUOVE da disegnare (~29)

### Pattern Adapter

| Da → A                                 | Tipo                                             | Estremi (molteplicità)             | Etichetta |
| -------------------------------------- | ------------------------------------------------ | ---------------------------------- | --------- |
| **CalendarGateway ⊳⤏ AdapterCalendar** | **realizzazione** (triangolo vuoto tratteggiato) | —                                  | —         |
| **AdapterCalendar → Calendar**         | associazione **navigabile** (→)                  | AdapterCalendar `1` / Calendar `1` | `adaptee` |

### Pattern Observer (marker + nuove realizzazioni)

| Da → A                            | Tipo          | Note                                         |
| --------------------------------- | ------------- | -------------------------------------------- |
| **Notifica ⊳⤏ Invito**            | realizzazione | nuova (Invito ora è un payload notificabile) |
| **Notifica ⊳⤏ RichiestaSupporto** | realizzazione |                                              |
| **Notifica ⊳⤏ PropostaCall**      | realizzazione |                                              |

> `Subject ⊳⤏ ServizioNotifiche`, `Observer ⊳⤏ Utente` e `Subject ◇— Observer : osservatori` **esistono già da iter‑2** (non si ridisegnano; cambia solo il tipo del payload, §2).

### Associazioni del mentoring (navigabili)

| Da → A                               | Estremi (molteplicità)                         | Etichetta       |
| ------------------------------------ | ---------------------------------------------- | --------------- |
| **RichiestaSupporto → Team**         | RichiestaSupporto `0..*` / Team `1`            | `richiedente`   |
| **RichiestaSupporto → Hackathon**    | RichiestaSupporto `0..*` / Hackathon `1`       | `contesto`      |
| **PropostaCall → Team**              | PropostaCall `0..*` / Team `1`                 | `destinatario`  |
| **PropostaCall → Utente**            | PropostaCall `0..*` / Utente `1`               | `proponente`    |
| **PropostaCall → RichiestaSupporto** | PropostaCall `0..*` / RichiestaSupporto `0..1` | `in risposta a` |

### Dipendenze verso le enum

- `RichiestaSupporto ⤏ StatoRichiesta : «use»`
- `PropostaCall ⤏ StatoCall : «use»`

### Dipendenze «use» EBC (Handler → service)

- `HandlerRichiedeSupporto → GestoreSupporto`
- `HandlerVisualizzaRichieste → GestoreSupporto`
- `HandlerProponeCall → GestoreCall`
- `HandlerAccettaCall → GestoreCall`
- `HandlerRifiutaCall → GestoreCall`

### Dipendenze «use» service → Repository

- `GestoreSupporto → RepoRichiestaSupporto`
- `GestoreSupporto → RepoIscrizione` ← **uso nuovo** di un Repo esistente (`iscrizioneInCorso`)
- `GestoreCall → RepoPropostaCall`

### Dipendenze «use» service → Entity / Observer / Adapter

- `GestoreSupporto → RichiestaSupporto`
- `GestoreSupporto → ServizioNotifiche`
- `GestoreCall → PropostaCall`
- `GestoreCall → RichiestaSupporto`
- `GestoreCall → Team` ← entità esistente (ricava il `destinatario` dalla richiesta)
- `GestoreCall → Hackathon` ← entità esistente (scope tramite staff)
- `GestoreCall → Incarico` ← entità esistente (`mentoreAssegnato`, ruolo = Mentore)
- `GestoreCall → CalendarGateway`
- `GestoreCall → ServizioNotifiche`

---

## 4. Cosa NON toccare (resta identico a iter‑2)

- Tutte le classi di iter‑1/iter‑2: 10 Handler, i service `GestoreTeam`/`GestoreHackathon`/`ServiceIscrizione`/`ServiceSottomissione`/`ServiceValutazione`/`GestoreInvito`, le **7 entità invariate** (`Team`/`Appartenenza`/`Hackathon`/`Incarico`/`Iscrizione`/`Sottomissione`/`Valutazione` — `Utente` e `Invito` invece sono toccate, §2), i **6 Repo invariati** (`RepoIscrizione` invece è toccato, §2), gli enum `RuoloStaff`/`StatoInvito`.
- Il **pattern State** dell'`Hackathon`: `abstract Stato` con guardie **e** transizioni (`avvia/iniziaValutazione/concludi`); `Stato <|-- InIscrizione/InCorso/InValutazione/Concluso`; attributo `Hackathon.stato : Stato` e associazione `Hackathon → Stato : stato corrente`. **In iter‑3 lo State non cambia** (D2).
- Le **classi‑associazione** `Incarico` / `Appartenenza` / `Iscrizione` (notazione classe‑associazione, NON reificate).
- La catena del valore `Iscrizione — Sottomissione : contiene`, `Sottomissione — Valutazione : riceve`, `Utente — Valutazione : redige`.
- Le associazioni di iter‑2 `Hackathon → Team : vincitore`, `Team — Invito : inviti`, `Invito → Utente : invitato`.

---

## 5. Riepilogo

- **17** classi nuove: 5 Handler · 2 service (`GestoreSupporto`, `GestoreCall`) · **3 Adapter** (`CalendarGateway` «interface», `AdapterCalendar` «adapter», `Calendar` «sistema esterno») · 1 interface marker (`Notifica`) · 2 Entity · 2 enum · 2 Repository.
- **6** classi esistenti modificate: `RepoIscrizione` (+`iscrizioneInCorso`) · `Subject`/`Observer`/`ServizioNotifiche`/`Utente` (ritipo payload `Invito`→`Notifica`) · `Invito` (realizza `Notifica`).
- **~29** relazioni nuove (2 Adapter + 3 realizzazioni `Notifica` + 5 associazioni mentoring + 2 dipendenze enum + 5 EBC «use» + 3 service→Repo «use» + 9 service→Entity/Observer/Adapter «use»).
- **1** modifica **non additiva** (generalizzazione payload Observer) · **0** rimozioni.

**Pattern in iter‑3:** **Adapter** (NUOVO, GoF strutturale: isola Calendar) · **Observer** (riusato da iter‑2 e **generalizzato** col marker `Notifica`) · **State** invariato. **GRASP** invariati (Controller, Creator, Information Expert, Pure Fabrication — di cui ora anche `CalendarGateway` —, Polymorphism).

### Decisioni rilevanti per il diagramma (rif. casi d'uso / council)

- **`confermaPrenotazione(proposta : PropostaCall)`** tenuto **fedele alla sequenza UC14** (la porta riceve la `PropostaCall` ed estrae il `riferimentoPrenotazione`). Mild domain‑leak accettato per tracciabilità diagramma↔sequenza; alternativa purista: `confermaPrenotazione(riferimentoPrenotazione : string)`.
- **`StatoRichiesta.Gestita/Chiusa`** presenti nell'enum ma non raggiunti da alcun UC di iter‑3: è un **problema aperto noto** (ciclo di vita della richiesta — "chi/quando la chiude", useCase §4.5), non un difetto del diagramma.
- Stereotipi **«adapter»** e **«sistema esterno»** registrati in `CONVENZIONI_DIAGRAMMI.md §2.1`.
