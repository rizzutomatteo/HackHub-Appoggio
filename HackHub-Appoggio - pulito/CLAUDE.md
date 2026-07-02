# CLAUDE.md

Questo è un progetto di **Ingegneria del Software** che adotta il **Processo Unificato (Unified Process)**: lo sviluppo è iterativo e incrementale, organizzato in iterazioni (es. `iterazione1/`) e nelle discipline tipiche del PU (requisiti, analisi, progettazione, implementazione, test).

- Il dominio applicativo è **HackHub**, una piattaforma web per la gestione di hackathon.
- Il **testo della traccia** del progetto si trova in `progetto.MD` (nella root). È la fonte autorevole sui requisiti, sugli attori e sui vincoli tecnici (Java → Spring Boot, almeno due design pattern). Va consultato, non modificato.

La **fonte di verità** di ogni diagramma delle classi è un file di testo **PlantUML** (`.puml`).

## Scelte deliberate — NON segnalarle come errori nelle revisioni future

Le seguenti caratteristiche sono **volute** e già valutate. Non vanno riproposte come problemi di correttezza/coerenza/completezza né "corrette" senza richiesta esplicita:

- **Molteplicità `Utente "1" -- "0..1" Team`** nei diagrammi di progettazione di iter. 3 e 4 (in analisi resta `1..*`).
- **Numerazione dei casi d'uso della 4a iterazione divergente** tra `CASI_USO_DETTAGLIATI.md` (UC16–UC21, Registrazione+Autenticazione unite) e `useCase4.puml`/diagrammi di sequenza (UC16–UC22, separate).
- **Riferimenti a file non presenti nel repo** (es. `CONVENZIONI_DIAGRAMMI.md`, `iterazione1/legacy/…`, `CASI_USO_REALIZZATI.md` in iter.4): citazioni intenzionali.
- **Doppio vocabolario EBC**: sequenze con boundary `Pagina*` e control `Gestore*`/`ServizioNotifiche`; classi con `Handler*` e `Service*`. Incluso il naming `Gestore*` della sola iter. 2 e le divergenze testo-vs-diagramma dell'iter. 4.
- **Stereotipi/lingua dell'analisi iter. 4** (`«Entity»` solo su `Pagamento`, classe-associazione `appartiene` minuscola) e **casing degli enum** diverso tra analisi (MAIUSCOLO) e progettazione (PascalCase).
- **Quirk trascritti fedelmente dai `.jpg` di Visual Paradigm**: `RepoTeam.salva(parameter)` senza nome, `RepoHackathon` vuoto in iter. 3, doppia rappresentazione `stato: Stato` (attributo) + associazione `stato corrente`, refusi nei `.jpg`/sequenze (es. `datalnizio`, `dimesioneMaxTeam`), smell di layering della porta `CalendarGateway`.
- **Accesso in lettura alle sottomissioni per il generico Membro dello Staff** non realizzato come UC a sé: raffinamento opzionale già dichiarato.

Restano invece **corretti** nei `.puml` di progettazione iter. 3 e 4: la composizione `Sottomissione "1" *-- "0..1" Valutazione : riceve` (verso ripristinato) e la firma `preparaProclamazione(hackathon : Hackathon)` (parametro reintrodotto).
