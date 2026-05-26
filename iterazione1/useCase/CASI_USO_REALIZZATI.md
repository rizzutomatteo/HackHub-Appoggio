# Casi d'Uso Realizzati — Prima Iterazione HackHub

Specifiche dei 5 casi d'uso della prima iterazione **così come effettivamente modellate** (Flow of Events in Visual Paradigm). Per la versione estesa "fully-dressed" con motivazioni e mappatura ai campi di Visual Paradigm vedi `CASI_USO_DETTAGLIATI.md`.

## Legenda delle convenzioni

- `SYSTEM` — il sistema (HackHub).
- `N.` — passo della sequenza principale.
- `N.a`, `N.b`, … — estensione (flusso alternativo) attivata in corrispondenza del passo `N`.
- `N.a.1`, `N.a.2`, … — passi interni dell'estensione.
- `#.a` — estensione attivabile **in qualunque momento** durante il caso d'uso.
- `jump to N` — il flusso ritorna al passo `N`.
- `exit Scenario` — il caso d'uso termina.

---

## Crea Hackathon

**Descrizione:** L'Organizzatore crea un nuovo hackathon definendone le informazioni essenziali; alla creazione l'hackathon entra nello stato "in iscrizione" e diventa visibile nell'elenco.

**Pre-condizioni:**

- L'attore è autenticato e ha il ruolo di Organizzatore
- l'hackathon non è ancora stato creato

**Post-condizioni:**

- Esiste un nuovo hackathon nello stato "in iscrizione".
- Le informazioni sono associate correttamente all'hackathon
- L'hackathon è elencato tra quelli consultabili.
- _Oppure_
- l'operazione viene annullata e l'hackathon non viene creato

**Flow of Events:**

1. il caso d'uso ha inizio quando l'Organizzatore richiede la creazione di un nuovo hackathon.
2. SYSTEM fa vedere la pagina di inserimento dati dell'hackathon
3. l'Organizzatore inserisce nome, regolamento, scadenza iscrizioni, data di inizio, data di fine, luogo, premio in denaro e dimensione massima del team.
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

- **#.a.** In qualunque momento l'Organizzatore annulla:
  - #.a.1. SYSTEM l'hackathon non viene creato e tutte le informazioni memorizzate vengono cancellate
  - #.a.2. il caso d'uso termina
  - #.a.3. exit Scenario

---

## Crea Team

**Descrizione:** Un Utente registrato crea un nuovo team, invitando eventualmente altri utenti. Chi crea il team ne diventa il primo membro (e amministratore).

**Pre-condizioni:**

- L'Utente è autenticato e non appartiene già ad alcun team.

**Post-condizioni:**

- Esiste un nuovo team persistito.
- L'Utente creatore risulta membro (ed amministratore) del team.
- Gli eventuali inviti agli altri utenti sono stati registrati/inviati.
- _oppure_
- l'operazione viene annullata

**Flow of Events:**

1. Il caso d'uso ha inizio quando l'Utente richiede la creazione di un nuovo team.
2. SYSTEM verifica che l'Utente non appartenga già ad un team.
3. SYSTEM presenta il modulo (nome del team ed elenco di utenti da invitare).
4. l'Utente inserisce il nome del team e seleziona gli utenti da invitare.
   - **4.a.** Nome del team mancante o già in uso:
     - 4.a.1. SYSTEM segnala l'errore
     - 4.a.2. jump to 4. l'Utente inserisce il...
5. l'Utente conferma
6. SYSTEM crea il team e vi associa l'Utente come membro/amministratore.
7. SYSTEM invia gli inviti agli utenti selezionati.
8. SYSTEM conferma la creazione del team.

- **#.a.** In qualunque momento l'Utente annulla: nessun team viene creato.
  - #.a.1. exit Scenario

---

## Iscrivi Team

**Descrizione:** Un Membro del team iscrive il proprio team a un hackathon che si trova nello stato "in iscrizione".

**Pre-condizioni:**

- L'attore è autenticato ed è membro di un team.
- Esiste un hackathon nello stato "in iscrizione".
- Il team non è già iscritto a quell'hackathon.

**Post-condizioni:**

- Il team risulta iscritto all'hackathon

**Flow of Events:**

1. Il caso d'uso ha inizio quando il Membro del team vuole iscrivere il proprio team ad un hackathon
2. il Membro del team seleziona un hackathon nello stato "in iscrizione"
3. il Membro del team richiede l'iscrizione del proprio team
4. SYSTEM verifica che l'hackathon sia ancora in iscrizione (iscrizioni aperte) e che il team non sia già iscritto.
   - **4.a.** L'hackathon non è più nello stato "in iscrizione" (iscrizioni chiuse):
     - 4.a.1. SYSTEM Il sistema rifiuta l'iscrizione e ne spiega il motivo.
     - 4.a.2. jump to 2. il Membro del team seleziona...
   - **4.b.** Il team è già iscritto a quell'hackathon:
     - 4.b.1. SYSTEM informa l'attore e non crea un duplicato.
     - 4.b.2. jump to 2. il Membro del team seleziona...
   - **4.c.** Il numero di membri del team supera la dimensione massima dell'hackathon:
     - 4.c.1. SYSTEM segnala il superamento del limite.
     - 4.c.2. exit Scenario
5. SYSTEM registra l'iscrizione del team all'hackathon.
6. SYSTEM conferma l'iscrizione.

- **#.a.** in qualunque momento il membro annulla l'operazione
  - #.a.1. exit Scenario

---

## Invia Sottomissione

**Descrizione:** Un Membro del team invia la sottomissione del proprio team per un hackathon a cui il team è iscritto

**Pre-condizioni:**

- L'attore è autenticato ed è membro di un team iscritto all'hackathon
- L'hackathon è nello stato "in corso"

**Post-condizioni:**

- La sottomissione del team per quell'hackathon è creata con data, ora e associata a team iscritto ad un certo hackathon

**Flow of Events:**

1. Il caso d'uso ha inizio quando un Membro del team vuole inviare una sottomissione
2. Membro del team apre il modulo per l'invio della sottomissione
3. SYSTEM mostra il modulo per l'invio
   - **3.a.** L'hackathon è passato a "in valutazione"
     - 3.a.1. SYSTEM mostra messaggio di errore
     - 3.a.2. il caso d'uso termina
     - 3.a.3. exit Scenario
4. Membro del team carica la sottomissione e la invia
5. SYSTEM chiede la conferma dell'invio
   - **5.a.** il Membro del team non da la conferma e annulla l'operazione
     - 5.a.1. jump to 3. SYSTEM mostra il modulo per...
6. Membro del team conferma l'invio
7. SYSTEM salva la sottomissione con la data e l'ora corrente

- **#.a.** Membro del team annulla l'operazione
  - #.a.1. SYSTEM riporta tutto alla situazione prima dell'avvio del caso d'uso
  - #.a.2. il caso d'uso termina
  - #.a.3. exit Scenario

---

## Valuta Sottomissione

**Descrizione:** Il Giudice assegnato all'hackathon valuta ciascuna sottomissione con un giudizio scritto e un punteggio compreso tra 0 e 10.

**Pre-condizioni:**

- Il Giudice è autenticato ed è assegnato all'hackathon.
- L'hackathon è nello stato "in valutazione".
- Esiste almeno una sottomissione da valutare.

**Post-condizioni:**

- La sottomissione selezionata ha una valutazione registrata
- composta da giudizio testuale e punteggio (0-10) attribuita dal Giudice.

> ⚠️ Nota: le Post-condizioni qui sopra riportano lo stesso testo delle Pre-condizioni (riportato verbatim come fornito). Probabilmente da rivedere: dovrebbero descrivere l'esito (es. "la sottomissione ha una valutazione registrata con giudizio scritto e punteggio 0-10").

**Flow of Events:**

1. Il caso d'uso ha inizio quando il Giudice intende valutare una sottomissione
2. SYSTEM mostra l'elenco delle sottomissioni.
3. il Giudice seleziona la sottomissione.
4. SYSTEM mostra i dettagli della sottomissione
   - **4.a.** La sottomissione è già stata valutata
     - 4.a.1. SYSTEM mostra la valutazione esistente e ne consente la modifica
5. il Giudice inserisce un giudizio scritto e un punteggio.
6. il Giudice conferma la valutazione.
7. SYSTEM verifica che il punteggio sia compreso tra 0 e 10.
   - **7.a.** Il punteggio è fuori dall'intervallo ammesso (< 0 o > 10)
     - 7.a.1. SYSTEM segnala l'errore
     - 7.a.2. jump to 4. SYSTEM mostra i dettagli della...
   - **7.b.** Il giudizio scritto è vuoto:
     - 7.b.1. SYSTEM segnala che il giudizio è obbligatorio
     - 7.b.2. jump to 4. SYSTEM mostra i dettagli della...
8. SYSTEM registra la valutazione e la associa alla sottomissione.
9. SYSTEM conferma la registrazione della valutazione.
