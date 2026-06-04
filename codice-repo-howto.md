# Repo del codice indipendente — come funziona

In questo progetto convivono **due repository Git completamente indipendenti**, che
**non sono collegati tra loro in alcun modo** (niente submodule, niente subtree):

| Repo                                          | Cosa traccia                                                           | git-dir                                                          | Remote                                |
| --------------------------------------------- | ---------------------------------------------------------------------- | ---------------------------------------------------------------- | ------------------------------------- |
| **Appoggio** (`claudio` → `HackHub-Appoggio`) | **TUTTO** il progetto: analisi, diagrammi, docs, **e anche il codice** | `claudio/.git`                                                   | `origin` su GitHub `HackHub-Appoggio` |
| **Codice** (ufficiale)                        | **SOLO** `iterazione1/codice/` (i sorgenti Java)                       | `…/ingegneria/codice-hackhub.git` (FUORI dall'albero di claudio) | da collegare al GitHub ufficiale      |

I due repo lavorano sugli **stessi file su disco** dentro `iterazione1/codice/`, ma con
storie e remote separati: un commit nel repo Appoggio comprende tutto il progetto, un
commit nel repo Codice comprende solo la cartella del codice.

## Perché la git-dir è fuori dall'albero

Se dentro `iterazione1/codice/` ci fosse un normale `.git` (cartella **o** file),
`claudio` lo interpreterebbe come "repo annidato" e **smetterebbe di tracciare i file
del codice** (li vedrebbe come un gitlink). Per evitarlo, il repo del codice tiene la
sua git-dir **fuori** dall'albero di `claudio`:

- **git-dir** (database del repo): `…/ingegneria/codice-hackhub.git`
- **work-tree** (file di lavoro): `…/claudio/iterazione1/codice`

Così dentro `iterazione1/codice/` **non c'è nessun `.git`**, `claudio` continua a
tracciare il codice come file normali, e i due repo restano del tutto separati.

## Il comando `codice`

Per operare sul repo del codice si usa il comando `codice` (in `~/.local/bin/codice`):
si comporta **esattamente come `git`**, ma agganciato automaticamente alla git-dir e al
work-tree del repo del codice. Funziona da qualsiasi cartella.

```bash
codice status                 # stato del repo del codice
codice add -A                 # stage di tutte le modifiche al codice
codice as matteo -m "msg"     # commit firmato (usa git-as: matteo|ale|mozzo)
codice log --oneline          # storia del solo codice
codice push origin main       # push sul repo ufficiale
```

Regola mentale: **`git …` = repo Appoggio (tutto)**, **`codice …` = repo Codice (solo
la cartella)**. Sono lo stesso file su disco, due storie diverse.

## Commit firmati e multi-account

`codice as <account>` è identico a `git as <account>` ma sul repo del codice: usa le
identità e le signing key dei tre account (`matteo`, `ale`, `mozzo`) descritte in
[`git-multi-account.md`](./git-multi-account.md). Vale tutto ciò che è scritto lì.

Per creare **più commit ripartiti tra i 3 account, in ordine logico e spalmati nel
tempo**, vale lo stesso playbook di [`commit-spalmati-howto.md`](./commit-spalmati-howto.md):
basta sostituire `git as …` con `codice as …` (e `git push` con `codice push`). La
funzione `commit_at` diventa:

```bash
commit_at () {  # $1=data ISO  $2=account  $3=file  $4=messaggio
  export GIT_AUTHOR_DATE="$1"; export GIT_COMMITTER_DATE="$1"
  codice add "$3"
  codice as "$2" -m "$4" || { echo "FALLITO $2 ($3)"; codice reset -q "$3"; exit 1; }
}
```

I percorsi dei file sono **relativi alla cartella `iterazione1/codice/`** (es.
`entity/Hackathon.java`, non `iterazione1/codice/entity/Hackathon.java`).

## Collegare il repo GitHub ufficiale (una tantum)

Il repo del codice esiste già in locale (con il commit iniziale di import). Per
collegarlo al GitHub ufficiale e pushare:

```bash
# 1. aggiungi il remote ufficiale (l'account di push e' deciso dall'alias SSH nell'URL:
#    git@github.com:…        -> matteo
#    git@github-alessandroacciarresi:… -> ale
#    git@github-alemozzoni:… -> mozzo)
codice remote add origin <URL-DEL-REPO-UFFICIALE>

# 2. primo push
codice push -u origin main
```

## Gotcha

- **Passphrase di matteo:** la signing key di matteo ha la passphrase; se i commit di
  matteo falliscono, carica la chiave nell'agent: `! ssh-add ~/.ssh/id_ed25519`
  (vedi `commit-spalmati-howto.md`).
- **Non confondere i due repo:** un `git commit` dentro `claudio` finisce nel repo
  Appoggio (tutto); un `codice commit` finisce nel repo Codice (solo la cartella).
- **Non spostare** `…/ingegneria/codice-hackhub.git` né la cartella
  `claudio/iterazione1/codice` senza aggiornare i percorsi in `~/.local/bin/codice`
  (e in `core.worktree` della git-dir).
- **Niente `.git` dentro `iterazione1/codice`:** se ne compare uno, qualcosa ha
  inizializzato un repo lì dentro per errore — va rimosso, altrimenti claudio si rompe.
