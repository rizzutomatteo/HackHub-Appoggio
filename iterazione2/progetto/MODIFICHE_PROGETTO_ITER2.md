# Modifiche al diagramma delle classi di PROGETTO — da iter‑1 a iter‑2

> **Scopo.** Checklist operativa di **tutto ciò che va aggiunto/cambiato** nel diagramma delle classi di progetto realizzato in Visual Paradigm (iter‑1, `CLASSI_PROGETTO_REALIZZATI.jpg`) per portarlo all'**iterazione 2**.
> **Fonte.** `iterazione2/progetto/CLASSI_PROGETTO_CHANGE2.puml` (versione coerente con le convenzioni di iter‑1).
> **Natura del delta.** È tutto **additivo**: nessuna classe/relazione di iter‑1 va rimossa o rinominata (iter‑2 è un soprainsieme).
> **Convenzioni.** Vedi `CONVENZIONI_DIAGRAMMI.md` (stereotipi «Handler»/«service»/«Entity»/«Repository»/«enumeration»/«interface»; classi-associazione mantenute; dipendenze «use»).

---

## 1. Classi NUOVE da creare (12)

### Boundary «Handler»

| Classe                           | Operazioni                                                                                                              |
| -------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| **HandlerAvviaHackathon**        | `+ avviaHackathon(hackathon : Hackathon) : void`                                                                        |
| **HandlerIniziaFaseValutazione** | `+ iniziaFaseValutazione(hackathon : Hackathon) : void`                                                                 |
| **HandlerProclamaVincitore**     | `+ apriProclamazione(hackathon : Hackathon) : void`<br>`+ proclama(hackathon : Hackathon, teamVincitore : Team) : void` |
| **HandlerInvitaMembro**          | `+ invitaMembro(team : Team, utenteInvitato : Utente) : void`                                                           |
| **HandlerAccettaInvito**         | `+ accettaInvito(invito : Invito) : void`                                                                               |

### Control «service»

| Classe                | Operazioni                                                                                                    |
| --------------------- | ------------------------------------------------------------------------------------------------------------- |
| **GestoreInvito**     | `+ invita(team : Team, utenteInvitato : Utente) : boolean`<br>`+ accetta(invito : Invito) : boolean`          |
| **ServizioNotifiche** | `+ registra(o : Observer) : void`<br>`+ rimuovi(o : Observer) : void`<br>`+ notifica(invito : Invito) : void` |

### Interfacce «interface» (pattern Observer)

| Classe       | Operazioni                                                                                                    |
| ------------ | ------------------------------------------------------------------------------------------------------------- |
| **Subject**  | `+ registra(o : Observer) : void`<br>`+ rimuovi(o : Observer) : void`<br>`+ notifica(invito : Invito) : void` |
| **Observer** | `+ aggiorna(invito : Invito) : void`                                                                          |

### Entity «Entity»

| Classe     | Attributi                                        | Operazioni                                   |
| ---------- | ------------------------------------------------ | -------------------------------------------- |
| **Invito** | `- dataInvito : date`<br>`- stato : StatoInvito` | `+ accetta() : void`<br>`+ rifiuta() : void` |

### Enum «enumeration»

| Classe          | Valori                               |
| --------------- | ------------------------------------ |
| **StatoInvito** | `Pendente`, `Accettato`, `Rifiutato` |

### Repository «Repository»

| Classe         | Operazioni                                                                                    |
| -------------- | --------------------------------------------------------------------------------------------- |
| **RepoInvito** | `+ esisteInvitoPendente(team : Team, u : Utente) : boolean`<br>`+ salva(inv : Invito) : void` |

---

## 2. Operazioni/attributi da AGGIUNGERE a classi ESISTENTI (5)

| Classe esistente     | Da aggiungere                                                                                                                                                                                                                                                     |
| -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **GestoreHackathon** | `+ avvia(hackathon : Hackathon) : boolean`<br>`+ iniziaFaseValutazione(hackathon : Hackathon) : boolean`<br>`+ preparaProclamazione(hackathon : Hackathon) : List<Sottomissione>`<br>`+ proclamaVincitore(hackathon : Hackathon, teamVincitore : Team) : boolean` |
| **Hackathon**        | `+ avvia() : boolean`<br>`+ iniziaFaseValutazione() : boolean`<br>`+ proclamaVincitore(teamVincitore : Team) : boolean`                                                                                                                                           |
| **Stato** (astratta) | `+ avvia() : Stato`<br>`+ iniziaValutazione() : Stato`<br>`+ concludi() : Stato` — sono le **transizioni** (prima c'erano solo le 3 guardie)                                                                                                                      |
| **Team**             | `+ aggiungiMembro(utente : Utente) : Appartenenza`                                                                                                                                                                                                                |
| **Utente**           | `+ aggiorna(invito : Invito) : void` (perché realizza `Observer`)                                                                                                                                                                                                 |

> **Stati concreti (opzionale).** Le transizioni dichiarate in `Stato` sono ridefinite nei 4 stati: `InIscrizione.avvia()` → `InCorso`; `InCorso.iniziaValutazione()` → `InValutazione`; `InValutazione.concludi()` → `Concluso`; negli altri stati la transizione non consentita è rifiutata (polimorfismo). Per il diagramma è sufficiente dichiararle nella classe astratta.

---

## 3. Relazioni NUOVE da disegnare

### Associazioni / pattern

| Da → A                           | Tipo                                             | Estremi (molteplicità)         | Etichetta     |
| -------------------------------- | ------------------------------------------------ | ------------------------------ | ------------- |
| **Hackathon → Team**             | associazione **navigabile** (freccia →)          | Hackathon `0..*` / Team `0..1` | `vincitore`   |
| **Team — Invito**                | associazione                                     | Team `1` / Invito `0..*`       | `inviti`      |
| **Invito → Utente**              | associazione **navigabile** (→)                  | Invito `0..*` / Utente `1`     | `invitato`    |
| **Invito ⤏ StatoInvito**         | dipendenza                                       | —                              | `«use»`       |
| **ServizioNotifiche ⊳⤏ Subject** | **realizzazione** (triangolo vuoto tratteggiato) | —                              | —             |
| **Utente ⊳⤏ Observer**           | **realizzazione**                                | —                              | —             |
| **Subject ◇— Observer**          | **aggregazione** (rombo vuoto lato Subject)      | Observer `0..*`                | `osservatori` |

### Dipendenze «use» EBC / persistenza (nuove)

Tratteggiata `⤏` con etichetta `«use»`:

- `HandlerAvviaHackathon → GestoreHackathon`
- `HandlerIniziaFaseValutazione → GestoreHackathon`
- `HandlerProclamaVincitore → GestoreHackathon`
- `HandlerInvitaMembro → GestoreInvito`
- `HandlerAccettaInvito → GestoreInvito`
- `GestoreHackathon → RepoSottomissione` ← **nuova** (la proclamazione legge le sottomissioni)
- `GestoreInvito → RepoInvito`
- `GestoreInvito → RepoUtente`
- `GestoreInvito → RepoTeam`
- `GestoreInvito → Invito`
- `GestoreInvito → Team`
- `GestoreInvito → ServizioNotifiche`

---

## 4. Cosa NON toccare (resta identico a iter‑1)

- Le 5 Handler originali, i 5 service originali (`GestoreTeam`/`GestoreHackathon`/`ServiceIscrizione`/`ServiceValutazione`/`ServiceSottomissione`), le 8 entità, i 6 Repo, l'enum `RuoloStaff`.
- Le **classi-associazione** `Incarico` / `Appartenenza` / `Iscrizione` (notazione classe-associazione, NON reificate).
- L'attributo `Hackathon.stato : Stato` e l'associazione `Hackathon → Stato : stato corrente`.
- La catena del valore in **composizione**: `Iscrizione *— Sottomissione : contiene`, `Sottomissione *— Valutazione : riceve`; `Utente — Valutazione : redige`.
- Generalizzazioni `Stato <|-- InIscrizione/InCorso/InValutazione/Concluso`.

> ⚠️ **Attenzione (sviste nel `CHANGE2.puml` da NON replicare in VP):** nel file sorgente erano rimaste 3 imprecisioni che in VP vanno lasciate come iter‑1 — la catena del valore è **composizione** (rombo pieno) non associazione semplice; `Hackathon.verificaDimensione` è **privato** `-`; `GestoreHackathon.creaHackathon` ritorna **boolean** (non `void`).

---

## 5. Riepilogo

- **12** classi nuove (5 Handler, 2 service, 2 interfacce, 1 Entity, 1 enum, 1 Repository).
- **5** classi esistenti a cui aggiungere operazioni (GestoreHackathon, Hackathon, Stato, Team, Utente).
- **~17** relazioni nuove (7 associazioni/pattern + ~12 dipendenze «use», di cui `GestoreHackathon → RepoSottomissione` su una classe esistente).
- **0** rimozioni.

**Pattern introdotti in iter‑2:** State **completato** (transizioni) · **Observer** (Subject/Observer/ServizioNotifiche/Utente). **GRASP** invariati.
