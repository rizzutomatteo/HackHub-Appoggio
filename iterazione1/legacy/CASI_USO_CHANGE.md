# Proposte di Modifica — `CASI_USO_REALIZZATI.md`

Questo file **propone** le modifiche a `CASI_USO_REALIZZATI.md` **senza applicarle**. Decidi tu
cosa accettare e poi riportalo sia nel file sia nel **modello Visual Paradigm** (la sorgente `.vpp`
è il deliverable valutato: `CASI_USO_REALIZZATI.md` ne è solo lo specchio testuale).

**Severità:** 🔴 critico · 🟠 importante · 🟡 minore.

**Riferimenti del corso usati:**

- `docs/UML_02_CasiUso.pdf` — slide 11 (sezioni del caso d'uso: Pre-condizioni = "condizioni vere
  prima di attivare", Post-condizioni = "cosa dovrà essere vero alla fine"); slide 13-15
  (convenzioni: forma attiva, ramificazioni con "Se", cicli `for`/`while` o rimandi, le estensioni
  possono avere pre/post-condizioni a sé stanti); slide 26 (esempio "Elabora Vendita": la sezione è
  **"Post-condizioni (Garanzie di successo)"**).
- `progetto.MD` — regole di dominio (aggiornamento sottomissione entro la scadenza; punteggio 0-10;
  composizione staff: 1 Giudice e ≥1 Mentore).

---

## Crea Hackathon

### 🟠 1 — Pre-condizione vacua

**Problema.** "l'hackathon non è ancora stato creato" è una tautologia: ogni creazione la soddisfa
per definizione. Una pre-condizione deve essere uno stato significativo verificabile prima
dell'attivazione (slide 11).

**Attuale**

```
**Pre-condizioni:**

- L'attore è autenticato e ha il ruolo di Organizzatore
- l'hackathon non è ancora stato creato
```

**Proposto**

```
**Pre-condizioni:**

- L'attore è autenticato e ha il ruolo di Organizzatore
```

### 🔴 2 — Validazione dopo la creazione (ordine illogico)

**Problema.** Il passo 6 "crea e salva" ha come estensione 6.a "Dati non validi → torna al passo 3":
il sistema _crea e poi_ segnala i dati invalidi. La validazione deve precedere la persistenza.
Inoltre 4.a.2 rimanda al passo 2 (perdendo i dati già inseriti) e 6.a.2 ha la forma ridondante
"SYSTEM Il sistema torna…".

**Attuale**

```
4. 'Organizzatore seleziona un Giudice e uno o più Mentori tra gli staff disponibili.
   - **4.a.** Nessun Giudice selezionato (o più di uno) / nessun Mentore
     - 4.a.1. SYSTEM impedisce la conferma e segnala il vincolo ("un Giudice e almeno un Mentore").
     - 4.a.2. jump to 2. SYSTEM fa vedere la pagina di...
5. L'Organizzatore conferma la creazione dell'hackathon
6. SYSTEM crea l'hackathon nello stato "in iscrizione" e lo salva.
   - **6.a.** Dati mancanti o non validi
     - 6.a.1. SYSTEM segnala i campi non validi.
     - 6.a.2. SYSTEM Il sistema torna al passo 3.
7. SYSTEM mostra il riepilogo dell'hackathon creato.
8. SYSTEM aggiunge l'hackathon alla lista pubblica degli hackathon
```

**Proposto**

```
4. L'Organizzatore seleziona un Giudice e uno o più Mentori tra gli staff disponibili.
   - **4.a.** Nessun Giudice selezionato (o più di uno) / nessun Mentore
     - 4.a.1. SYSTEM impedisce la conferma e segnala il vincolo ("un Giudice e almeno un Mentore").
     - 4.a.2. jump to 4. L'Organizzatore seleziona un Giudice e uno o più Mentori...
5. L'Organizzatore conferma la creazione dell'hackathon
6. SYSTEM valida i dati inseriti.
   - **6.a.** Dati mancanti o non validi
     - 6.a.1. SYSTEM segnala i campi non validi.
     - 6.a.2. jump to 3. L'Organizzatore inserisce i dati dell'hackathon...
7. SYSTEM crea l'hackathon nello stato "in iscrizione" e lo salva.
8. SYSTEM mostra il riepilogo dell'hackathon creato.
9. SYSTEM aggiunge l'hackathon alla lista pubblica degli hackathon.
```

### 🟡 3 — Refuso e forma passiva nell'estensione di annullamento

**Problema.** Refuso "4. 'Organizzatore" (manca la L); l'annullamento usa forma passiva ("non viene
creato", "vengono cancellate") — slide 14 raccomanda la forma attiva — e ripete "il caso d'uso
termina" + "exit Scenario" (ridondante per la legenda).

**Attuale**

```
- **#.a.** In qualunque momento l'Organizzatore annulla:
  - #.a.1. SYSTEM l'hackathon non viene creato e tutte le informazioni memorizzate vengono cancellate
  - #.a.2. il caso d'uso termina
  - #.a.3. exit Scenario
```

**Proposto** (il fix della L è già incluso nella proposta 2)

```
- **#.a.** In qualunque momento l'Organizzatore annulla:
  - #.a.1. SYSTEM non crea l'hackathon; nessun dato viene persistito.
  - #.a.2. exit Scenario
```

---

## Crea Team

### 🟠 1 — Estensione 2.a mancante

**Problema.** Il passo 2 verifica che l'Utente non appartenga già a un team, ma non c'è
l'estensione che gestisce il fallimento della verifica. (In alternativa, se "non appartiene a un
team" resta una pre-condizione, il passo 2 andrebbe rimosso: slide 11, le pre-condizioni sono
assunte vere e non si ri-verificano nel flusso. Consiglio l'estensione 2.a, più esplicita.)

**Attuale**

```
2. SYSTEM verifica che l'Utente non appartenga già ad un team.
3. SYSTEM presenta il modulo (nome del team ed elenco di utenti da invitare).
```

**Proposto**

```
2. SYSTEM verifica che l'Utente non appartenga già ad un team.
   - **2.a.** L'Utente appartiene già ad un team:
     - 2.a.1. SYSTEM nega l'operazione e informa l'Utente (invariante "un solo team").
     - 2.a.2. exit Scenario
3. SYSTEM presenta il modulo (nome del team ed elenco di utenti da invitare).
```

_(Post-condizioni success-only: vedi sezione "Trasversale".)_

---

## Iscrivi Team

### 🟡 1 — Nome non coerente con il diagramma

**Problema.** Il titolo è "Iscrivi Team" (imperativo), ma il diagramma e gli altri documenti usano
"Iscrive Team" (terza persona, la convenzione scelta in `REVISIONE_USE_CASE.md` §7.2). Inoltre
in 4.a.1 c'è la forma ridondante "SYSTEM Il sistema rifiuta…".

**Attuale**

```
## Iscrivi Team
...
     - 4.a.1. SYSTEM Il sistema rifiuta l'iscrizione e ne spiega il motivo.
```

**Proposto**

```
## Iscrive Team
...
     - 4.a.1. SYSTEM rifiuta l'iscrizione e ne spiega il motivo.
```

_(Post-condizioni: vedi sezione "Trasversale" — è già success-only, basta uniformare l'etichetta.)_

---

## Invia Sottomissione

### 🟠 1 — Manca il percorso di aggiornamento (requisito non modellato)

**Problema.** `progetto.MD` (§Membro del Team) richiede: _"Fino a tale scadenza il membro può
ancora aggiornare la sottomissione."_ Il flusso copre solo il primo invio: non c'è l'estensione
"esiste già una sottomissione → aggiorna". Va resa esplicita (sia in descrizione sia nel flusso).
Sistemare anche il refuso "non da" → "non dà" e l'articolo mancante ("Membro del team apre" → "Il
Membro del team apre").

**Attuale (descrizione e flusso)**

```
**Descrizione:** Un Membro del team invia la sottomissione del proprio team per un hackathon a cui il team è iscritto
...
5. SYSTEM chiede la conferma dell'invio
   - **5.a.** il Membro del team non da la conferma e annulla l'operazione
     - 5.a.1. jump to 3. SYSTEM mostra il modulo per...
6. Membro del team conferma l'invio
7. SYSTEM salva la sottomissione con la data e l'ora corrente
```

**Proposto**

```
**Descrizione:** Un Membro del team invia la sottomissione del proprio team per un hackathon a cui il team è iscritto. Lo stesso caso d'uso copre sia il primo invio sia gli aggiornamenti successivi: la sottomissione resta modificabile finché l'hackathon è nello stato "in corso".
...
5. SYSTEM chiede la conferma dell'invio.
   - **5.a.** Il Membro del team non dà la conferma e annulla l'operazione:
     - 5.a.1. jump to 3. SYSTEM mostra il modulo per...
6. Il Membro del team conferma l'invio.
7. SYSTEM salva la sottomissione con la data e l'ora corrente.
   - **7.a.** Esiste già una sottomissione del team per quell'hackathon:
     - 7.a.1. SYSTEM aggiorna la sottomissione esistente (consentito perché l'hackathon è ancora "in corso") e ne aggiorna la data-ora.
```

**Post-condizione (proposta)**

```
- La sottomissione del team per quell'hackathon è creata (o aggiornata) e persistita con data-ora, associata al team e all'hackathon.
```

---

## Valuta Sottomissione

### 🔴 1 — Post-condizioni = copia delle pre-condizioni

**Problema (il più grave).** Le post-condizioni ripetono testualmente le pre-condizioni (lo segnala
già la nota ⚠️ presente nel file). Devono invece descrivere l'**esito**: slide 11 ("cosa dovrà
essere vero alla fine") e slide 26 ("Post-condizioni = Garanzie di successo").

**Attuale**

```
**Post-condizioni:**

- Il Giudice è autenticato ed è assegnato all'hackathon.
- L'hackathon è nello stato "in valutazione".
- Esiste almeno una sottomissione da valutare.

> ⚠️ Nota: le Post-condizioni qui sopra riportano lo stesso testo delle Pre-condizioni ...
```

**Proposto**

```
**Post-condizioni (garanzia di successo):**

- La sottomissione selezionata ha una valutazione registrata, composta da giudizio testuale e punteggio (0-10), attribuita dal Giudice.
```

### 🟠 2 — Manca il ciclo "per ogni sottomissione"

**Problema.** `progetto.MD` (§Giudice): il Giudice valuta _ciascuna_ sottomissione. Il flusso, così
com'è, valuta una sola sottomissione per esecuzione. Slide 14 ammette esplicitamente i cicli
(`for`/`while` o rimandi). Aggiungere il ciclo dopo l'ultimo passo.

**Proposto** (in coda al Flow of Events, dopo il passo 9)

```
_Il Giudice ripete i passi 3-9 (ciclo while) finché restano sottomissioni da valutare._
```

_(Opzionale: uniformare "il Giudice" → "Il Giudice" a inizio passo, slide 13.)_

---

## Trasversale — Post-condizioni come "garanzia di successo"

**Problema / risposta alla domanda "le post-condizioni sono solo di successo o anche le
alternative?".** Nel materiale del corso la sezione **Post-condizioni = garanzia di successo**
(slide 11: "cosa dovrà essere vero alla fine"; slide 26: "Post-condizioni (Garanzie di successo)").
Gli esiti alternativi (es. annullamento) si esprimono **nelle estensioni** (slide 15: "le sequenze
alternative possono prevedere pre/post-condizioni a sé stanti"), non mescolati nella post-condizione
principale.

Oggi i 5 casi d'uso sono incoerenti: _Crea Hackathon_ e _Crea Team_ aggiungono "_Oppure_ … annullato"
alla post-condizione, gli altri no. **Proposta: post-condizioni success-only su tutti e 5**, con
l'annullamento già coperto dall'estensione `#.a`.

**Esempio — Crea Hackathon**

Attuale

```
**Post-condizioni:**

- Esiste un nuovo hackathon nello stato "in iscrizione".
- Le informazioni sono associate correttamente all'hackathon
- L'hackathon è elencato tra quelli consultabili.
- _Oppure_
- l'operazione viene annullata e l'hackathon non viene creato
```

Proposto

```
**Post-condizioni (garanzia di successo):**

- Esiste un nuovo hackathon persistito nello stato "in iscrizione".
- L'hackathon ha esattamente un Giudice e almeno un Mentore associati.
- Le informazioni sono associate correttamente all'hackathon.
- L'hackathon è elencato tra quelli consultabili.
```

**Esempio — Crea Team**

Attuale

```
**Post-condizioni:**

- Esiste un nuovo team persistito.
- L'Utente creatore risulta membro (ed amministratore) del team.
- Gli eventuali inviti agli altri utenti sono stati registrati/inviati.
- _oppure_
- l'operazione viene annullata
```

Proposto

```
**Post-condizioni (garanzia di successo):**

- Esiste un nuovo team persistito.
- L'Utente creatore risulta membro (ed amministratore) del team.
- Gli eventuali inviti agli altri utenti sono stati registrati/inviati.
```

Per _Iscrivi Team_, _Invia Sottomissione_ e _Valuta Sottomissione_ basta rinominare l'etichetta in
**"Post-condizioni (garanzia di successo)"** (sono già success-only).

---

## Riepilogo

| Caso d'uso            | 🔴  | 🟠  | 🟡  |
| --------------------- | --- | --- | --- |
| Crea Hackathon        | 1   | —   | 2   |
| Crea Team             | —   | 1   | —   |
| Iscrivi Team          | —   | —   | 1   |
| Invia Sottomissione   | —   | 1   | —   |
| Valuta Sottomissione  | 1   | 1   | —   |
| Trasversale (post-c.) | —   | 1   | —   |

I 2 punti 🔴 (post-condizioni di _Valuta_, ordine validazione di _Crea Hackathon_) sono i più
importanti da riportare nel modello Visual Paradigm.
