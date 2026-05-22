# Revisione Use Case Diagram — Tracking Problemi

Riferimento: `iterazione1/useCase/useCase.jpg` (versione finale, ex `useCase4`) confrontato con `progetto.MD` e `docs/UML_02_CasiUso.pdf`. (Storico versioni: useCase1 → useCase4.)

**Stati possibili** (modifica il marker per aggiornare):

- `[ ]` **APERTO** — da risolvere
- `[x]` **RISOLTO** — corretto nel diagramma
- `[~]` **OK COSÌ** — decisione esplicita di tenere com'è (con motivazione nelle Note)

**Legenda priorità**: 🔴 bloccante · 🟠 importante · 🟡 minore

> La §6 (aspetti corretti) è inclusa come riferimento e non richiede azione.

---

## 1. Errori critici di notazione UML

### 1.1 🔴 `<<extend>>` usato tra attori (BLOCCANTE)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: relazioni tra attori ora con generalizzazione (triangolo vuoto), niente più `<<extend>>`
- **🔄 Nota (useCase4)**: la generalizzazione `Utente ──▷ Visitatore` è poi stata **rimossa** (vedi §1.3 e §9); restano le generalizzazioni staff (▷ Membro staff) e `Amministratore Team ▷ Membro del team`.

Nel diagramma compaiono frecce con lo stereotipo `<<extend>>` tra:

- `Membro staff` ←→ `Organizzatore`, `Giudice`, `Mentore`
- `Amministratore Team` ←→ `Membro del team`
- `utente` ←→ `Visitatore`

**Errore**: `<<extend>>` è uno stereotipo che si applica solo a relazioni tra casi d'uso (vedi `UML_02_CasiUso.pdf` slide 23 "estensione"). Per esprimere che un attore è una specializzazione di un altro va usata la generalizzazione (freccia con punta a triangolo vuoto, vedi `UML_01_Introduzione.pdf` slide 7 e `UML_02_CasiUso.pdf` slide 20-22).

**Correzione**: rimuovere lo stereotipo `<<extend>>` e cambiare la freccia in generalizzazione UML (punta a triangolo vuoto, senza testo).

### 1.2 🔴 Direzione della generalizzazione invertita: Amministratore Team / Membro del team

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: `Amministratore Team` ora generalizza `Membro del team` (direzione corretta)
- **✅ Confermato in useCase4**: generalizzazione mantenuta.

Dal diagramma sembra che `Membro del team` sia una specializzazione di `Amministratore Team`. È semanticamente errato: l'Amministratore è un Membro del team con permessi aggiuntivi → IS-A Membro del team. La freccia (a triangolo vuoto) deve partire da `Amministratore Team` e puntare a `Membro del team`. Così l'Amministratore eredita tutti i casi d'uso del Membro del team (visualizza specifiche, aggiorna sottomissione, abbandona team…) e aggiunge i suoi (Rende amministratore, Gestisci Candidature, Elimina Team).

> ⚠️ Nota: `progetto.MD` non parla esplicitamente di un "Amministratore Team". È una scelta tua. È ragionevole (qualcuno deve poter eliminare il team o promuovere altri), ma vale la pena dichiararlo formalmente nei requisiti o in un commento sul diagramma, altrimenti il professore potrebbe contestarlo come attore introdotto senza giustificazione.

### 1.3 🟠 Relazione tra Visitatore e Utente

- [x] **Stato**: RISOLTO (aggiornato in useCase4)
- **✅ Fatto in useCase2**: introdotta `Utente ──▷ Visitatore`.
- **🔄 Aggiornato in useCase4**: la generalizzazione `Utente ──▷ Visitatore` è stata **rimossa**. Motivo: avrebbe fatto ereditare all'Utente i casi d'uso `Registrazione` e `Autenticazione`, che un attore già autenticato non esegue (la generalizzazione regge solo se il figlio fa _tutto_ ciò che fa il padre). Decisione: Visitatore, Utente e Membro del team sono **attori distinti** e l'autenticazione è modellata come **pre-condizione / «include»** (cfr. `docs/UML_02_CasiUso.pdf` slide 22). Vedi §9.

`progetto.MD`: il Visitatore è non autenticato, l'Utente è autenticato: sono due ruoli distinti. La generalizzazione era stata introdotta per condividere l'accesso alle informazioni pubbliche, ma comportava l'ereditarietà impropria di `Registrazione`/`Autenticazione`; per questo in useCase4 è stata rimossa a favore della pre-condizione di autenticazione.

---

## 2. Casi d'uso assegnati all'attore sbagliato

### 2.1 🔴 Crea Hackathon sotto utente (CRITICO)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: `Crea Hackathon` spostato sotto `Organizzatore` (opzione A)

`progetto.MD` §Organizzatore: "L'Organizzatore è un membro dello staff che crea nuovi hackathon definendo le informazioni essenziali…". Quindi `Crea Hackathon` non può essere un caso d'uso di utente generico.

Due interpretazioni possibili — scegline una e rendi coerente:

- **(A)** Sposta `Crea Hackathon` sotto `Organizzatore` (o `Membro staff`). Significa che gli Organizzatori esistono già nel sistema e ne creano altri.
- **(B)** Mantieni `Crea Hackathon` per `utente`, con il vincolo che chi crea diventa automaticamente Organizzatore di quell'hackathon. Da specificare come post-condizione del caso d'uso.

L'interpretazione (B) è più realistica per una piattaforma aperta, ma va dichiarata esplicitamente.

### 2.2 🟠 Invia Sottomissione sotto Amministratore Team

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: `Invia Sottomissione` spostato sotto `Membro del team`

`progetto.MD` §Membro del Team: "Il Membro del Team può consultare tutti gli hackathon, iscrivere il proprio team a un hackathon, ed inviare la sottomissione entro la scadenza prevista". Il diritto è del Membro, non solo dell'Amministratore. Spostalo sotto `Membro del team` (così sarà ereditato anche dall'Amministratore, una volta corretta la generalizzazione del §1.2).

### 2.3 🟠 Invia Candidatura Team sotto utente

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: rinominato `Iscrive Team` e spostato sotto `Membro del team`

Per `progetto.MD` l'iscrizione del team a un hackathon è fatta dal Membro del Team. Spostalo sotto `Membro del team` (o `Amministratore Team` se ritieni che solo l'admin possa iscrivere il team — chiariscilo). Inoltre il termine "Candidatura" suggerisce un processo di approvazione che `progetto.MD` non descrive: valuta di rinominarlo `Iscrivi Team a Hackathon`.

### 2.4 🟠 Visualizza Tutte Sottomissioni per Membro staff

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: rinominato `Visualizza Sottomissioni Hackathon Assegnato`

`progetto.MD` §Membro dello Staff: "Può accedere alle sottomissioni dei team, ma solo per gli hackathon cui è assegnato come staff". Il nome corrente è fuorviante (implica accesso globale). Rinomina in `Visualizza Sottomissioni Hackathon Assegnato` o aggiungi un vincolo `{solo hackathon in cui è staff}` sull'associazione.

---

## 3. Ridondanze e nomi ambigui

### 3.1 🟠 Tripla sovrapposizione sulle sottomissioni

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: `Aggiungi Sottomissione` eliminato (restano `Invia Sottomissione` + `Modifica Sottomissioni`)
- **🔄 Aggiornato in useCase4**: anche `Modifica Sottomissioni` è stato **rimosso**; l'aggiornamento della sottomissione è ora **fuso in `Invia Sottomissione`** (estensione "esiste già una sottomissione → aggiorna", entro lo stato "in corso"). Vedi `CASI_USO_DETTAGLIATI.md` UC04 e §9.

Hai tre casi d'uso che fanno (apparentemente) cose sovrapposte:

| Caso d'uso               | Attore              | Cosa fa?                             |
| ------------------------ | ------------------- | ------------------------------------ |
| `Invia Sottomissione`    | Amministratore Team | crea la sottomissione iniziale?      |
| `Aggiungi Sottomissione` | Membro del team     | crea la sottomissione iniziale?      |
| `Aggiorna Sottomissione` | Membro del team     | modifica una sottomissione esistente |

`progetto.MD` distingue due operazioni: invio (creazione) e aggiornamento (modifica fino alla scadenza). `Invia Sottomissione` e `Aggiungi Sottomissione` sono duplicati → tieni solo `Invia Sottomissione` (sotto Membro del team) ed elimina `Aggiungi Sottomissione`.

### 3.2 🟠 Casi d'uso "Gestisci X" (anti-pattern di scomposizione funzionale)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: `Gestisci Hackathon`/`Gestisci staff`/`Gestisci Candidature`/`Gestisce Call` rimossi o spezzati; resta solo `Gestisci Segnalazioni` (accettabile)

`UML_02_CasiUso.pdf` slide 24 avverte esplicitamente contro la scomposizione funzionale. I tuoi `Gestisci Hackathon`, `Gestisci staff`, `Gestisci Segnalazioni`, `Gestisci Candidature`, `Gestisci inviti`, `Gestisce Call` sono nomi vaghi che spesso raggruppano logica senza essere essi stessi un "processo di business elementare" (test BPE, slide 17).

Suggerimenti concreti:

- `Gestisci Hackathon` → rimuovi; tieni solo i casi specifici già presenti (`Modifica info Hackathon`, `Elimina Hackathon`, `Avvia Hackathon`, `Termina Hackathon`).
- `Gestisci staff` → rimuovi se l'unica azione è `Invita staff`; oppure spezza in `Aggiungi Mentore`, `Rimuovi staff`.
- `Gestisci inviti` → spezza in `Accetta Invito al Team` + `Rifiuta Invito al Team` (entrambi richiesti da `progetto.MD`).
- `Gestisci Candidature` → spezza in `Approva Candidatura` + `Rifiuta Candidatura`.
- `Gestisci Segnalazioni` → potrebbe restare se rappresenta davvero un processo unitario (visualizza segnalazioni + decide); altrimenti spezzalo.
- `Gestisce Call` → cosa fa che `Proporre Call` non faccia già? Se è solo "visualizza calendario call" rinominalo in modo chiaro.

### 3.3 🟡 Visualizza specifiche progetto (ambiguo)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: rinominato `Visualizza Traccia Progetto`

`Visualizza specifiche progetto` (Membro del team) → cosa intendi? Regolamento dell'hackathon? Brief del progetto? Rinomina in `Visualizza Regolamento Hackathon` o `Visualizza Dettagli Hackathon`.

### 3.4 🟡 Visualizza sottomissioni (ambiguo)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: il caso d'uso ambiguo `Visualizza sottomissioni` (Membro del team) è stato rimosso

`Visualizza sottomissioni` (Membro del team) → quali? Solo la propria? Anche quelle di altri team? `progetto.MD` non concede al Membro del team di vedere sottomissioni di altri team. Chiarisci o limita.

---

## 4. Casi d'uso mancanti

### 4.1 🟠 Registrazione (Visitatore)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: aggiunto `Registrazione` sotto Visitatore

`progetto.MD` §Visitatore: "Non può accedere ad altre funzionalità finché non effettua la registrazione e l'accesso". Nel diagramma manca completamente. Va aggiunto sotto Visitatore (assieme ad `Autenticazione`, che pure per il Visitatore al primo accesso è necessaria).

### 4.2 🟠 Invita Utenti al Team (utente che crea il team / Amministratore Team)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: aggiunto `Invita Membro` sotto Amministratore Team

`progetto.MD` §Utente: "può creare un nuovo team invitando altri utenti della piattaforma". Hai `Crea Team` ma non è ovvio che includa l'invito. Aggiungi un caso d'uso esplicito `Invita Utente al Team`, eventualmente legato a `Crea Team` con `<<include>>`.

### 4.3 🟡 Visualizzazione del risultato / dell'esito (Membro del team, eventualmente Visitatore)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase3**: aggiunto `Visualizza Risultati` sotto Membro del team

Non c'è un caso d'uso per "vedere chi ha vinto" né per "vedere la propria valutazione". Sono utili e probabilmente impliciti, ma renderli espliciti dà completezza.

### 4.4 🟡 (opzionale) Aggiungi Mentore a Hackathon esistente

- [~] **Stato**: OK COSÌ
- **Motivazione (2026-05-22)**: `Invita staff` copre l'aggiunta di staff (mentori e giudici), anche dopo la creazione

`progetto.MD` §Organizzatore: "l'Organizzatore può aggiungere più Mentori all'hackathon anche successivamente alla sua creazione". È un'azione distinta dall'invito staff iniziale: o la copri esplicitamente, o spieghi nella descrizione di `Invita staff` che vale anche dopo la creazione.

---

## 5. Coerenza con il ciclo di vita dell'hackathon

### 5.1 🟡 Termina Hackathon (ambiguo)

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2 → useCase3**: aggiunto `Inizia Fase Valutazione`; in useCase3 `Termina Hackathon` è stato rimosso (ciclo di vita pulito — vedi §8.2)

`progetto.MD` definisce 4 stati: in iscrizione → in corso → in valutazione → concluso. Le transizioni nel tuo diagramma:

| Transizione                 | Caso d'uso              | Stato nel diagramma |
| --------------------------- | ----------------------- | ------------------- |
| (creazione) → in iscrizione | `Crea Hackathon`        | ✓ implicito         |
| in iscrizione → in corso    | `Avvia Hackathon`       | ✓                   |
| in corso → in valutazione   | `Termina Hackathon` (?) | ⚠️ ambiguo          |
| in valutazione → concluso   | `Proclama Vincitore`    | ✓                   |

`Termina Hackathon` ha un nome ambiguo: termina cosa? Le iscrizioni? Le sottomissioni? Apre la valutazione? Suggerimento: rinomina in `Chiudi Sottomissioni` o `Apri Valutazione`. Inoltre verifica chi è l'attore: dal regolamento sembra l'Organizzatore (giusto), ma potrebbe anche essere automatico in base alle date — se è così, va modellato come attore `Tempo`/scheduler.

---

## 6. Aspetti corretti / scelte valide (riferimento — nessuna azione)

Ricontrollati su useCase4: ancora validi.

- Identificazione attori principali aderente a `progetto.MD` (Membro staff, Organizzatore, Mentore, Giudice, Membro del team, utente, Visitatore).
- Sistemi esterni `Calendar` e `Sistema Pagamento` correttamente modellati come attori secondari posizionati a destra del confine sistema.
- `Paga Montepremi` legato a `Sistema Pagamento` ✓
- `Proporre Call` collegato a `Calendar` ✓ (la prenotazione delegata al sistema esterno è coerente con il requisito).
- `Segnala Team` (Mentore) ↔ `Gestisci Segnalazioni` (Organizzatore) coerente con il flusso descritto in `progetto.MD`.
- `Valuta Sottomissione` per il Giudice ✓ (anche se manca dettaglio: la valutazione è giudizio + punteggio 0-10, ma questo livello di dettaglio sta nella descrizione testuale del caso d'uso, non nel diagramma — vedi `UML_02_CasiUso.pdf` slide 11).
- `Abbandona Team` per il Membro del team: utile (gestisce la regola "un utente appartiene a un solo team").

---

## 7. Errori ortografici / nomi non uniformi

### 7.1 🟡 Typo `Hackaton`

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: corretto in `Modifica info Hackathon`

`Modifica info Hackaton` → `Modifica info Hackathon` (manca una 'h').

### 7.2 🟡 Forma imperativa non uniforme

- [~] **Stato**: OK COSÌ
- **Motivazione (2026-05-22)**: convenzione in terza persona accettata. (Restano all'imperativo `Gestisci Segnalazioni` e `Rimuovi Staff`: per piena uniformità → `Gestisce`/`Rimuove`, ma è facoltativo.)

Nomi da uniformare alla forma imperativa (coerenza con `Crea`, `Invita`, `Segnala`, `Valuta`…):

- `Gestisce Call` → `Gestisci Call`
- `Proporre Call` → `Proponi Call`
- `Rende amministratore` → `Promuovi a Amministratore` o `Designa Amministratore`

(Il bullet "`Aggiungi Sottomissione` → da eliminare" è tracciato in §3.1.)

### 7.3 🟡 Capitalizzazione attore `utente`

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase2**: attore ora `Utente`

`utente` (minuscolo) → `Utente` (consistenza con gli altri attori che iniziano in maiuscolo).

---

## 8. Nuovi problemi emersi in useCase2 (risolti in useCase3)

### 8.1 🟡 Typo `Acetta Invito`

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase3**: corretto in `Accetta Invito`

Il caso d'uso `Acetta Invito` (sotto Utente) ha un refuso: manca una 'c'.

### 8.2 🟡 Ridondanza `Termina Hackathon` vs `Inizia Fase Valutazione`

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase3**: rimosso `Termina Hackathon`; resta solo `Inizia Fase Valutazione` (ciclo di vita pulito: Crea → Avvia → Inizia Fase Valutazione → Proclama Vincitore)

Dopo l'aggiunta di `Inizia Fase Valutazione`, ora coesistono due casi d'uso che sembrano coprire la stessa transizione (`in corso` → `in valutazione`): `Termina Hackathon` e `Inizia Fase Valutazione`. Se rappresentano lo stesso passaggio di stato, tienine una sola; se sono distinti (es. "Termina Hackathon" chiude le sottomissioni e "Inizia Fase Valutazione" apre il giudizio), chiarisci i due nomi/transizioni.

### 8.3 🟡 Carattere vagante `i` vicino a Giudice

- [x] **Stato**: RISOLTO
- **✅ Fatto in useCase3**: carattere `i` vicino a Giudice rimosso

Vicino all'attore `Giudice` compare un carattere/etichetta isolato `i`, probabilmente un artefatto creato per errore in Visual Paradigm. Va rimosso.

---

## 9. Aggiornamenti in useCase4 (post-discussione su attori e autenticazione)

Dopo una discussione sulla correttezza delle generalizzazioni tra attori, in `useCase4` sono state apportate due modifiche rispetto a `useCase3`:

1. **Rimozione delle generalizzazioni tra Visitatore, Utente e Membro del team.** Sono ora **attori distinti**. L'autenticazione non è più "ereditata": `Registrazione`/`Autenticazione` restano sul Visitatore e l'essere autenticati per i casi d'uso UC01-UC05 è una **pre-condizione** (o «include» `Autenticazione`), come da `docs/UML_02_CasiUso.pdf` slide 22. Generalizzazioni rimaste: Organizzatore/Giudice/Mentore ▷ Membro staff; Amministratore Team ▷ Membro del team.
2. **Rimozione di `Modifica Sottomissioni`.** L'aggiornamento della sottomissione è fuso in `Invia Sottomissione` (vedi §3.1 e `CASI_USO_DETTAGLIATI.md` UC04).

## Decisioni e questioni aperte

- ✅ **Tutti i punti chiusi; diagramma aggiornato a useCase.jpg (2026-05-22).**
- Polish facoltativo residuo (non bloccante): uniformare `Gestisci Segnalazioni`/`Rimuovi Staff` alla terza persona; maiuscola coerente `Staff`/`staff`.
- ⚠️ Promemoria da §1.2: valuta di dichiarare l'attore `Amministratore Team` nei requisiti / in una nota sul diagramma, dato che `progetto.MD` non lo menziona esplicitamente.

---

## Riepilogo conteggio

- Totale problemi: **22**
- Risolti: **20**
- OK così: **2** (4.4, 7.2)
- Aperti: **0** — ✅ tutti chiusi su useCase.jpg (2026-05-22)
- Aggiornamenti successivi (raffinamenti, non nuovi problemi): vedi **§9** (attori/autenticazione; rimozione `Modifica Sottomissioni`).
