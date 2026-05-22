# Casi d'Uso della Prima Iterazione — HackHub

## 1. Scopo del documento

Questo documento spiega **quali casi d'uso** del progetto HackHub vengono sviluppati nella **prima iterazione** e **perché**. La selezione non è arbitraria: segue la metodologia del Processo Unificato (UP) adottata nel corso e i criteri di prioritizzazione dei casi d'uso.

Riferimenti: requisiti in `progetto.MD`; modello dei casi d'uso in `diagrammi/useCase4.jpg`.

---

## 2. Metodologia: perché si sceglie un sottoinsieme

Il corso adotta il **Processo Unificato (UP)**, che è (cfr. `docs/02_Processo.pdf`):

- **Iterativo e incrementale** — il sistema non si sviluppa tutto in una volta, ma per iterazioni successive; ogni iterazione produce una _baseline_ eseguibile e testata.
- **Guidato dai casi d'uso** (use-case driven) — i casi d'uso sono l'unità di pianificazione e guidano analisi, progettazione e test.
- **Incentrato sull'architettura** (architecture-centric) — le prime iterazioni servono a stabilizzare l'architettura.
- **Guidato dal rischio** (risk-driven) — si affrontano per primi gli elementi a maggior rischio.

Di conseguenza **non si sviluppano tutti i casi d'uso subito**:

- Nella fase di **Avvio (Inception)** si cattura e stabilizza solo il **10–20% dei casi d'uso più importanti** (`02_Processo.pdf`, fase Inception).
- Nella prima iterazione di **Elaborazione (Elaboration)** l'obiettivo è creare una **baseline architetturale eseguibile** che **dimostra che i rischi principali sono stati identificati e risolti** (`02_Processo.pdf`, fase Elaboration). La tabella 7.1 di `UML_02_CasiUso.pdf` mostra che in queste prime iterazioni si analizza in dettaglio e si implementa solo una piccola parte (~10%) dei casi d'uso più significativi.

### Criteri di selezione (ranking dei casi d'uso)

Si scelgono i casi d'uso che massimizzano (Larman, _Applicare UML e i pattern_, testo del corso):

1. **Copertura architetturale** — casi d'uso che obbligano a costruire tutta la dorsale del sistema (presentazione → servizio → dominio → persistenza). L'architettura a strati è descritta in `05_ProgettazioneArchitetturale.pdf`.
2. **Valore di business** — casi d'uso che rappresentano il cuore funzionale della piattaforma.
3. **Rischio** — casi d'uso che risolvono presto le incertezze tecniche (modello di dominio, macchina a stati, regole di business).

---

## 3. I 4 casi d'uso scelti

| #   | Caso d'uso           | Attore primario | Criterio dominante                     |
| --- | -------------------- | --------------- | -------------------------------------- |
| 1   | Crea Hackathon       | Organizzatore   | copertura architetturale + fondante    |
| 2   | Crea Team            | Utente          | valore + invariante di dominio         |
| 3   | Invia Sottomissione  | Membro del team | valore + regola di business (scadenza) |
| 4   | Valuta Sottomissione | Giudice         | valore + chiusura del ciclo            |

**1. Crea Hackathon (Organizzatore).** È il caso d'uso fondante: crea l'entità centrale del dominio (l'hackathon, con nome, regolamento, scadenza iscrizioni, date, luogo, premio, dimensione massima del team, giudice e mentori) e dà inizio alla sua macchina a stati (`in iscrizione → in corso → in valutazione → concluso`). Senza un hackathon nessun altro caso d'uso ha senso. Ha la **massima copertura architetturale**: costringe a realizzare l'entità più ricca del dominio, la sua persistenza, il servizio applicativo e l'interfaccia.

**2. Crea Team (Utente).** È l'unità di partecipazione alla piattaforma. Esercita la relazione Utente↔Team e soprattutto l'**invariante di dominio** "un utente può appartenere a un solo team". È prerequisito di qualunque sottomissione.

**3. Invia Sottomissione (Membro del team).** È il caso d'uso che **produce il valore** della piattaforma: il team consegna il proprio lavoro. Esercita una **regola di business critica**: la sottomissione si invia _entro la scadenza_ e resta aggiornabile _fino ad essa_. Presuppone il passo collegato **Iscrive Team** (iscrizione del team all'hackathon), che lega Team↔Hackathon ed è incluso nella stessa fetta.

**4. Valuta Sottomissione (Giudice).** Chiude il ciclo del valore: il Giudice valuta ogni sottomissione con un **giudizio scritto e un punteggio compreso tra 0 e 10**. Esercita il vincolo sul punteggio e lo stato "in valutazione" dell'hackathon.

---

## 4. La "fetta verticale" (walking skeleton)

I 4 casi d'uso non sono scelti isolatamente: insieme formano **un percorso eseguibile end-to-end**, lo scheletro funzionante della piattaforma.

```
Organizzatore         Utente / Membro del team                 Giudice
     │                          │                                 │
Crea Hackathon ─▶ Crea Team ─▶ Iscrive Team ─▶ Invia Sottomissione ─▶ Valuta Sottomissione
(in iscrizione)              (team → hackathon)  (entro la scadenza)   (giudizio + voto 0–10)
```

Questo percorso attraversa **tutti gli strati architetturali**:

- **Presentazione** (CLI / API REST) per ogni azione;
- **Servizio / Applicazione** che orchestra le operazioni;
- **Dominio** (Hackathon, Team, Utente, Sottomissione, Valutazione) con le sue regole;
- **Persistenza** dei dati.

E coinvolge **tutti gli attori primari** del sistema (Organizzatore, Utente/Membro del team, Giudice). È esattamente la **baseline architetturale eseguibile** richiesta dalla fase di Elaborazione UP: alla fine della prima iterazione si avrà un sistema piccolo ma _funzionante e testato_ che dimostra che l'architettura regge e i rischi principali sono risolti.

> **Transizioni di stato.** In questa iterazione il ciclo di vita dell'hackathon avanza in modo **automatico, guidato dalle date** (in iscrizione → in corso → in valutazione), così la fetta è eseguibile con i soli casi d'uso elencati. `Avvia Hackathon` e `Inizia Fase Valutazione`, pur presenti nel diagramma, diventeranno casi d'uso espliciti in iterazioni successive (cfr. `CASI_USO_DETTAGLIATI.md` §1.4).

---

## 5. Caso d'uso di supporto: Registrazione / Autenticazione

`Registrazione` e `Autenticazione` (attore **Visitatore**) **non** sono contati tra i 4 principali, ma vanno realizzati insieme ad essi perché sono **prerequisito trasversale** di ogni azione autenticata. Esercitano lo **strato di sicurezza**, che in un'architettura a strati è uno dei livelli di base (`05_ProgettazioneArchitetturale.pdf`: la security come strutturazione a strati). Possono essere modellati come pre-condizione o `<<include>>` dei casi d'uso principali.

---

## 6. Regole di business esercitate dalla prima iterazione

La fetta scelta non è solo "tante schermate": mette alla prova le regole più delicate del dominio, ed è questo che riduce il rischio.

- **Scadenza della sottomissione** — invio e aggiornamento consentiti solo entro la scadenza → _Invia Sottomissione_.
- **Punteggio 0–10** — vincolo sul valore della valutazione → _Valuta Sottomissione_.
- **Un utente, un solo team** — invariante → _Crea Team_.
- **Ciclo di vita dell'hackathon** — stati e transizioni → _Crea Hackathon_ (avvia lo stato "in iscrizione").

---

## 7. Casi d'uso rimandati alle iterazioni successive (e perché)

| Caso d'uso                                                   | Motivo del rinvio                                                                                                                  |
| ------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------- |
| Proclama Vincitore + Paga Montepremi                         | Fine del ciclo (dipendono dalle valutazioni completate); `Paga Montepremi` è integrazione esterna (Sistema Pagamento), simulabile. |
| Propone Call / Accetta-Rifiuta Proposta Call                 | Flusso secondario (mentoring) + integrazione esterna (Calendar).                                                                   |
| Segnala Team / Gestisci Segnalazioni                         | Funzione di moderazione, non sul percorso principale.                                                                              |
| Invita staff / Rimuovi Staff, Invita Membro / Accetta Invito | Gestione di staff e membri: supporting, non necessari per la dorsale.                                                              |
| Visualizza Risultati, Visualizza lista / info pubbliche      | Viste di sola lettura, a basso rischio.                                                                                            |

> **Nota sul rischio (integrazioni esterne).** Calendar e Sistema di Pagamento sono integrazioni esterne, un rischio tecnico classico che UP suggerirebbe di affrontare presto. In questo progetto, però, tali sistemi sono tipicamente _simulati/stub_, quindi il rischio reale è basso; il rischio maggiore da risolvere subito è il **modello di dominio + ciclo di vita + persistenza**, coperto dalla dorsale. Se in seguito una delle due integrazioni si rivelasse incerta, le si potrà dedicare uno _spike_ in un'iterazione successiva.

---

## 8. Prossimo passo

Per ciascuno dei 4 casi d'uso scelti, scriverne la **specifica nel formato dettagliato** (`UML_02_CasiUso.pdf` slide 11):

- Nome, ID, Breve descrizione
- Attori (primari e secondari)
- Pre-condizioni
- Sequenza principale degli eventi (numerata)
- Post-condizioni
- Sequenze alternative / di errore

Questo è il livello di dettaglio che la fase di Elaborazione UP richiede per il ~10–20% dei casi d'uso prioritari (`UML_02_CasiUso.pdf`, tabella 7.1).

---

**Riferimenti ai documenti del corso:**

- `progetto.MD` — requisiti e attori di HackHub
- `diagrammi/useCase4.jpg` — modello dei casi d'uso
- `docs/02_Processo.pdf` — UP, fasi Inception/Elaboration, iterazioni
- `docs/UML_02_CasiUso.pdf` — formato dei casi d'uso (slide 11), impegno per iterazione (tabella 7.1)
- `docs/05_ProgettazioneArchitetturale.pdf` — architettura a strati, requisiti qualitativi
