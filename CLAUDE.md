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

## Convenzioni diagrammi delle classi (PlantUML)

La **fonte di verità** di ogni diagramma delle classi è un file di testo **PlantUML** (`.puml`), non un'immagine né un file binario di Visual Paradigm. Il testo è leggibile/diffabile e versionabile; l'immagine renderizzata e il modello in Visual Paradigm sono derivati per la verifica visiva e per il deliverable ufficiale.

### Standard OBBLIGATORIO (da ora vale per OGNI diagramma del progetto)

Il diagramma delle classi di analisi `iterazione1/analisi/CLASSI_ANALISI_CHANGE.puml` è il **riferimento**: ogni diagramma futuro deve riprodurne questi risultati.

1. **Motore di layout**: `!pragma layout elk` (per i diagrammi a grafo: classi, oggetti, componenti, ecc.) → layout ortogonale pulito.
2. **Leggibilità (requisiti rigidi, da controllare sul render):**
   - **nessuna linea intrecciata**, nemmeno tra tipi diversi (associazioni, composizioni, dipendenze, connettori di nota);
   - **nessun allungamento inutile** delle linee;
   - **linee dritte** (ELK ortogonale), mai curve;
   - **nessuna scritta** (nome o molteplicità) sovrapposta o **estremamente vicina** ad altro.
3. **Relazioni**: ogni relazione ha un **nome**; molteplicità su **entrambe** le estremità.
4. **Vincoli non esprimibili graficamente** → nella **legenda** (niente note appese che si incrociano).
5. **Verifica visiva obbligatoria**: si **renderizza e si guarda** il PNG/SVG e si itera il layout finché valgono i punti 2-4. Un diagramma non è "fatto" finché il render non è pulito.
6. **Rendering & condivisione**: render con `./scripts/render-diagrams.sh` (solo Docker; ELK richiede Java 17, già nel container). Si **committano PNG+SVG** accanto al `.puml` per la visione del team.
7. **Mai stravolgere il modello per la grafica**: si cambia solo il layout, mai entità/relazioni/molteplicità. Se un **ciclo** del modello impone un arco lungo inevitabile, lo si instrada sul margine (senza incroci) e si rifinisce la posizione in **Visual Paradigm**.

Workflow:

1. Il diagramma viene scritto/aggiornato come `.puml` (segue le regole `*REALIZZATI` / `*CHANGE`: es. `CLASS_DIAGRAM_REALIZZATI.puml` non si tocca, le modifiche vanno in `CLASS_DIAGRAM_CHANGE.puml`).
2. Si renderizza in PNG/SVG per la verifica visiva (estensione PlantUML di VS Code, `plantuml.jar` da CLI con Graphviz, o server PlantUML online).
3. L'utente ricrea/raffina il diagramma in **Visual Paradigm** per il deliverable.

**Rendering e visione (progetto condiviso):**

- Le immagini renderizzate (PNG/SVG) sono **committate** accanto al `.puml`, così tutto il team le vede senza installare nulla (es. `iterazione1/analisi/CLASSI_ANALISI_HackHub.png`).
- Per **rigenerarle** dopo una modifica al `.puml`: `./scripts/render-diagrams.sh` — richiede **solo Docker** (Java 17 + ELK + Graphviz stanno nel container `plantuml/plantuml`, non serve installarli in locale).
- Nota: il layout `!pragma layout elk` (per azzerare gli incroci) è compilato per **Java 17**; i renderer locali con Java più vecchio falliscono (`UnsupportedClassVersionError`). Per questo il render canonico passa da Docker; l'anteprima diretta in VS Code può non funzionare → usa lo script o apri l'immagine committata.

**Scheletro file** (un file per diagramma, con legenda in testa):

```
@startuml NomeDiagramma
!pragma layout elk
hide empty members
hide circle

class Hackathon {
  -nome: String
  -stato: Stato
  +proclamaVincitore(t: Team): void
}
@enduml
```

**Visibilità** (sempre esplicita): `+` public, `-` private, `#` protected, `~` package.
**Membri**: attributi `-nome: Tipo`; operazioni `+metodo(param: Tipo): Ritorno`.

**Stile analisi vs progettazione**: nei diagrammi delle classi di **analisi** (come il riferimento) si usa lo **stile domain model** — **niente operazioni**, tipi e visibilità **omessi** (UML_03 slide 13), relazioni reificate come **classi-associazione**. Visibilità, tipi e operazioni (lo scheletro qui sopra) si aggiungono in fase di **progettazione**.

**Mappa univoca delle relazioni** (sintassi PlantUML fissata per evitare ambiguità):

- Generalizzazione (eredità): `Super <|-- Sub` — freccia vuota verso il padre.
- Realizzazione (interfaccia): `Interface <|.. Classe` — tratteggiata.
- Associazione: `A "1" -- "*" B : ruolo` — sempre con molteplicità.
- Associazione navigabile: `A --> "*" B` — la punta indica chi conosce chi.
- Aggregazione: `Intero o-- "*" Parte` — rombo **vuoto** sul lato **Intero**.
- Composizione: `Intero *-- "1..*" Parte` — rombo **pieno** sul lato **Intero**.
- Dipendenza: `A ..> B` — tratteggiata, transitoria.

Regole anti-errore:

- Il rombo (`o` aggregazione / `*` composizione) sta **sempre** sul lato del contenitore/Intero.
- Ogni associazione/aggregazione/composizione porta **sempre** la molteplicità su entrambe le estremità.
- I termini e le notazioni vanno allineati a `docs/UML_03_Class.pdf` e `docs/UML_04_Relazioni.pdf` (vedi sezione successiva).

## Documentazione di riferimento

Per qualsiasi scelta progettuale (notazione UML, processo, requirements engineering, qualità, architettura, design pattern, ecc.) fare riferimento **esclusivamente** a:

1. I materiali del corso nella cartella **`docs/`** (PDF su Processo, Requirements Engineering, Qualità, Progettazione Architetturale, V&V/Manutenzione, UML, GRASP, Design Patterns, Spring Boot, Git, ...).
2. In aggiunta, eventualmente, la **documentazione online ufficiale di Visual Paradigm** (lo strumento usato per la modellazione UML).

Non introdurre convenzioni, notazioni o pattern che non siano supportati da queste fonti.
