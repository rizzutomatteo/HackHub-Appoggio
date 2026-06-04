# Multi-account GitHub — istruzioni per Claude

Questo repo è gestito da un utente che lavora con **più account GitHub dalla stessa macchina**. Quando l'utente ti chiede di fare commit o push, devi usare l'account corretto seguendo esattamente queste istruzioni. Non improvvisare, non riconfigurare il sistema, non rigenerare chiavi: tutto è già impostato globalmente.

---

## Account disponibili

| Alias    | Nome git               | Email                       | Alias SSH (host)              | Chiave privata                           |
| -------- | ---------------------- | --------------------------- | ----------------------------- | ---------------------------------------- |
| `matteo` | `Matteo Rizzuto`       | `matteorizzuto04@gmail.com` | `github.com`                  | `~/.ssh/id_ed25519`                      |
| `ale`    | `alessandroacciarresi` | `aleacc2004@gmail.com`      | `github-alessandroacciarresi` | `~/.ssh/id_ed25519_alessandroacciarresi` |
| `mozzo`  | `alemozzoni`           | `alemozzoni@gmail.com`      | `github-alemozzoni`           | `~/.ssh/id_ed25519_alemozzoni`           |

Tutte le chiavi pubbliche sono già caricate sui rispettivi account GitHub sia come **Authentication key** sia come **Signing key**. Le firme sono verificabili in locale tramite `~/.ssh/allowed_signers`.

---

## Setup già attivo sulla macchina (NON ricrearlo)

- `~/.ssh/config` contiene gli alias `github.com`, `github-alessandroacciarresi`, `github-alemozzoni`.
- Git globale ha `gpg.format=ssh`, `commit.gpgsign=true`, `tag.gpgsign=true`. **Ogni commit viene firmato automaticamente.**
- `~/.local/bin/git-as` è il comando unico per committare con identità diversa senza alterare la config del repo.
- `~/.ssh/allowed_signers` riconosce le firme di tutti e tre gli account.

Se uno di questi pezzi manca, **fermati e segnalalo all'utente** invece di ricostruirli — significa che sei in un ambiente diverso da quello previsto.

---

## Regole vincolanti

1. **Mai modificare `user.name`/`user.email` globali** (`git config --global ...`). Non serve: usa `git as` o config locale al repo.
2. **Mai rigenerare chiavi SSH** (`ssh-keygen`). Le chiavi esistono già nei path elencati sopra.
3. **Mai installare GPG né cambiare `gpg.format`.** Il signing è già SSH.
4. **Mai fare `git commit` senza `-S` o senza `git as`** quando l'utente ha indicato un account specifico. Il commit deve sempre essere firmato.
5. **Non assumere quale account usare.** Se l'utente non lo dice esplicitamente, chiedi (vedi sezione "Scelta dell'account").
6. **Author e push possono essere account diversi solo se l'utente lo richiede esplicitamente.** Per default, autore del commit = account del push.

---

## Scelta dell'account

Prima di committare, identifica quale account usare con questo ordine di priorità:

1. **Istruzione esplicita nel messaggio corrente** dell'utente ("commit come ale", "fai push con mozzo").
2. **Config locale al repo** (`git config user.email`): se è già impostata a una delle tre email, usa quell'account.
3. **Remote URL di `origin`**: se è `git@github-alessandroacciarresi:...` → `ale`; `git@github-alemozzoni:...` → `mozzo`; `git@github.com:...` → `matteo`.
4. **Chiedi all'utente** se nessuno dei segnali sopra è chiaro. Non scegliere a caso.

---

## Comandi da usare

### Commit firmato come un account specifico

```bash
git as matteo -m "messaggio"
git as ale    -m "messaggio"
git as mozzo  -m "messaggio"
```

Lo script `git as` è equivalente a:

```bash
git -c user.name="<nome>" -c user.email="<email>" -c user.signingkey="<chiave.pub>" commit -S "<args>"
```

Non altera la config locale del repo, quindi puoi committare con account diversi nello stesso repo senza side-effect.

Tutti gli argomenti dopo l'alias vengono passati a `git commit`. Esempi:

```bash
git as ale -am "fix"           # stage tracked + commit
git as mozzo --amend --no-edit # amend dell'ultimo commit (mantiene msg)
git as matteo                  # apre l'editor
```

### Aggiungere i remote per push multi-account

Il primo `clone` di solito ha un solo `origin`. Per pushare verso un altro account dello stesso repo aggiungi un remote nominato in modo riconoscibile:

```bash
git remote add ale-remote   git@github-alessandroacciarresi:alessandroacciarresi/REPO.git
git remote add mozzo-remote git@github-alemozzoni:alemozzoni/REPO.git
```

Convenzioni:

- `origin` resta puntato all'account "principale" del repo (di solito chi lo possiede su GitHub).
- Per gli altri account usa nomi espliciti tipo `ale-remote`, `mozzo-remote`, oppure il nome dell'account.

### Push

```bash
git push origin       <branch>   # account principale del repo
git push ale-remote   <branch>   # account ale
git push mozzo-remote <branch>   # account mozzo
```

**L'account del push è quello dell'alias SSH nell'URL del remote**, non quello dell'autore del commit. Vedi sezione successiva.

---

## Author vs Push — chiarimento

| Cosa                                    | Da cosa è determinato                                                |
| --------------------------------------- | -------------------------------------------------------------------- |
| Nome che appare nella cronologia GitHub | `user.name` + `user.email` del commit (impostati da `git as`)        |
| Badge **Verified**                      | firma SSH (signing key) corrispondente all'account dell'email autore |
| Diritto di scrivere sul repo            | account autenticato via SSH nell'URL del remote di push              |

Quindi è perfettamente possibile (e normale) che:

- L'autore del commit sia `ale` e il push venga fatto da `matteo` → il commit appare attribuito a `ale`, ma è `matteo` che ha le credenziali per scrivere.

In questo repo, **per default usa lo stesso account sia per commit che per push**. Se l'utente vuole disaccoppiare i due ruoli te lo dirà esplicitamente.

---

## Workflow tipico in una conversazione

Quando l'utente dice "committa e pusha come `<account>`":

1. Identifica l'alias (`matteo` | `ale` | `mozzo`).
2. Se il remote per quell'account non esiste, aggiungilo (`git remote add ...`).
3. `git as <account> -m "<messaggio>"`
4. `git push <remote-account> <branch>`
5. Conferma con `git log -1 --show-signature` che la firma sia `Good "git" signature for <email-attesa>`.

Esempio completo:

```bash
git remote -v | grep -q '^ale-remote' || \
    git remote add ale-remote git@github-alessandroacciarresi:alessandroacciarresi/REPO.git

git as ale -m "feat: aggiunge X"
git push ale-remote main

git log -1 --show-signature
```

---

## Cosa fare se qualcosa non torna

- **`Permission denied (publickey)` durante push** → l'utente probabilmente non ha ancora dato write access a quell'account su quel repo. Segnalalo, non forzare.
- **Badge `Unverified` su GitHub** → o l'email dell'autore non è verificata su quell'account, o la signing key non è stata caricata. Mostra all'utente quale dei due verificare; non rifirmare con un account diverso per "aggirare" il problema.
- **`git: 'as' is not a git command`** → il sistema non è quello previsto (manca `~/.local/bin/git-as`). Fermati e segnalalo.
- **Il commit risulta autore di un account diverso da quello richiesto** → hai usato `git commit` invece di `git as`. Fai `git commit --amend` con `git as <account-corretto> --amend --no-edit` PRIMA del push se il commit non è ancora pushato.
