# CLAUDE.md

## Il progetto

Questo è un progetto di **Ingegneria del Software** che adotta il **Processo Unificato (Unified Process)**: lo sviluppo è iterativo e incrementale, organizzato in iterazioni (es. `iterazione1/`) e nelle discipline tipiche del PU (requisiti, analisi, progettazione, implementazione, test).

- Il dominio applicativo è **HackHub**, una piattaforma web per la gestione di hackathon.
- Il **testo della traccia** del progetto si trova in `progetto.MD` (nella root). È la fonte autorevole sui requisiti, sugli attori e sui vincoli tecnici (Java → Spring Boot, almeno due design pattern). Va consultato, non modificato.

## Regole sui file (valgono in OGNI punto del progetto)

### Cartelle `legacy/`

Tutto ciò che si trova dentro una cartella `legacy/` è **materiale vecchio**.

- Può essere **consultato** se serve come riferimento storico.
- **NON è aggiornato**: non rispecchia necessariamente lo stato attuale del progetto.
- **NON va mai modificato né aggiornato.**

### File `*REALIZZATI`

Tutti i file il cui nome termina con **`REALIZZATI`** (es. `CASI_USO_REALIZZATI.md`) sono le **implementazioni aggiornate dell'utente**.

- Sono la **fonte di verità** corrente.
- Vanno **solo consultati**: **non devono mai essere toccati, modificati o sovrascritti.**
- Se servono modifiche, **NON editare il file `*REALIZZATI`**. Crea invece un nuovo file con lo **stesso nome ma con suffisso `CHANGE`** al posto di `REALIZZATI`.
  - Esempio: per proporre modifiche a `CASI_USO_REALIZZATI.md` → creare `CASI_USO_CHANGE.md`.
  - Il file `*CHANGE` contiene le proposte di modifica; sarà l'utente a decidere se integrarle nel `*REALIZZATI`.

## Documentazione di riferimento

Per qualsiasi scelta progettuale (notazione UML, processo, requirements engineering, qualità, architettura, design pattern, ecc.) fare riferimento **esclusivamente** a:

1. I materiali del corso nella cartella **`docs/`** (PDF su Processo, Requirements Engineering, Qualità, Progettazione Architetturale, V&V/Manutenzione, UML, GRASP, Design Patterns, Spring Boot, Git, ...).
2. In aggiunta, eventualmente, la **documentazione online ufficiale di Visual Paradigm** (lo strumento usato per la modellazione UML).

Non introdurre convenzioni, notazioni o pattern che non siano supportati da queste fonti.
