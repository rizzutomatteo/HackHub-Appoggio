# Commit multi-account spalmati nel tempo — playbook per Claude

Questo file spiega a Claude Code **come creare una serie di commit** ripartiti tra i
tre account (`matteo`, `ale`, `mozzo`), che **seguono l'ordine logico** con cui i
file/feature sono stati creati e con **date/ore spalmate** in un intervallo scelto
dall'utente, per poi pushare. È il flusso già collaudato in questo repo.

Per le identità, le chiavi e le regole di base degli account vedi
[`git-multi-account.md`](./git-multi-account.md). Qui si dà per scontato quel setup.

---

## Quando attivare questo flusso

Quando l'utente dice qualcosa come:

- "committa questi file usando i 3 account, in ordine logico, spalmati su questa settimana"
- "fai N commit ripartiti equamente tra matteo/ale/mozzo tra il GG/MM e il GG/MM e pusha"
- "crea la cronologia dei commit seguendo l'ordine in cui sono stati fatti i file, distribuiti tra i 3"

Se l'utente **non specifica** uno di questi parametri, chiediglielo (vedi sotto). Non
inventare date nel futuro.

---

## Parametri da fissare prima di iniziare

1. **Quali file / in che ordine logico.** Di default: l'ordine di creazione naturale
   (prima i documenti/note, poi i dati di esempio, poi gli script, infine i runner).
   Un commit = un file (o un gruppo coerente). I commit vanno in ordine **cronologico
   crescente** che rispecchia l'ordine logico.
2. **Quali account e con quale ripartizione.** Default richiesto dall'utente: **equa**.
   Per essere davvero equa, conviene un numero di commit **multiplo di 3** (es. 6 → 2 a
   testa). Assegna gli account a rotazione `matteo → ale → mozzo → matteo → ...`.
3. **Intervallo temporale.** Es. "questa settimana". Oggi è il limite superiore: **non
   retrodatare nel futuro**. Spalma le date/ore in modo plausibile (orari da giornata
   lavorativa, più commit al giorno vanno bene), sempre in ordine crescente.
4. **Repo e remote di push.** Di default il repo già usato qui: `hackhub-uml`, con i
   remote `origin` (matteo), `ale-remote` (ale), `mozzo-remote` (mozzo). Il push si fa
   una volta sola via `origin` (matteo possiede il repo); l'autore dei commit è
   indipendente dal remote di push.

---

## Pre-flight check (SEMPRE, prima di committare)

```bash
cd <repo>

# 1. Il repo e i remote ci sono?
git remote -v

# 2. Lo strumento di commit firmato esiste?
command -v git-as            # deve stampare ~/.local/bin/git-as

# 3. Quali chiavi hanno passphrase? E l'agent è carico?
ssh-add -l                   # elenca le identità caricate nell'agent
for k in id_ed25519 id_ed25519_alessandroacciarresi id_ed25519_alemozzoni; do
  if ssh-keygen -y -P "" -f ~/.ssh/$k >/dev/null 2>&1; \
    then echo "$k: SENZA passphrase"; else echo "$k: CON passphrase"; fi
done
```

### ⚠️ Gotcha critico: la chiave di `matteo` ha la passphrase

La signing key di **matteo** (`~/.ssh/id_ed25519`) è protetta da passphrase; quelle di
`ale` e `mozzo` no. In una sessione non interattiva Claude **non può** digitarla: se
l'agent SSH è vuoto, **i commit di matteo falliscono** con
`failed to write commit object` / `cannot open /dev/tty`.

Se `ssh-add -l` non mostra la chiave di matteo, **fermati e chiedi all'utente** di
caricarla lui, eseguendo nel prompt (il prefisso `!` la esegue nella sessione):

```
! ssh-add ~/.ssh/id_ed25519
```

Poi riverifica con `ssh-add -l` prima di procedere. Non procedere finché la chiave di
matteo non è nell'agent, altrimenti la cronologia esce sbagliata.

### ⚠️ Gotcha critico: controllare l'exit code di OGNI commit

Se un commit fallisce (es. firma), il file resta **in stage** e finisce inglobato nel
commit successivo di un altro account → ripartizione ed equità rotte. Quindi **ogni
commit deve verificare l'exit code e fermarsi al primo errore**.

---

## Esecuzione

Usa questa funzione: imposta sia `GIT_AUTHOR_DATE` sia `GIT_COMMITTER_DATE` (servono
entrambe per retrodatare davvero), fa lo stage del **singolo** file, committa firmato
con `git as`, e **si ferma** se la firma fallisce.

```bash
cd <repo>
set -e

commit_at () {  # $1=data ISO  $2=account  $3=file  $4=messaggio
  export GIT_AUTHOR_DATE="$1"
  export GIT_COMMITTER_DATE="$1"
  git add "$3"
  if ! git as "$2" -m "$4" >/dev/null 2>&1; then
    echo "FALLITO commit di $2 ($3) — fermo." >&2
    git reset -q "$3"        # togli dallo stage per non inquinare il commit dopo
    exit 1
  fi
  echo "OK  $1  $2  $3"
}

# Esempio: 6 commit, 2 per account, lun→mer, ordine logico crescente
commit_at "2026-06-01T09:30:00" matteo NOTE.md      "docs: appunti iniziali"
commit_at "2026-06-01T14:10:00" ale    esempio1.txt "add: primo file di esempio"
commit_at "2026-06-02T10:20:00" mozzo  esempio2.txt "add: secondo file di esempio"
commit_at "2026-06-02T16:45:00" matteo hello.py     "feat: script hello"
commit_at "2026-06-03T11:15:00" ale    addio.py     "feat: script addio"
commit_at "2026-06-03T17:40:00" mozzo  run.sh       "chore: script per eseguire gli esempi"
```

Note:

- `git as <account>` è equivalente a un `git commit -S` con `user.name/email/signingkey`
  dell'account, **senza** toccare la config del repo. Tutti gli argomenti dopo l'alias
  vanno a `git commit`.
- Un file per commit: assegnalo nello `git add` esplicito, non usare `git add -A`.

---

## Verifica (prima e dopo il push)

```bash
# Ordine, autori, date
git log --oneline -6 --format='%h | %ci | %an <%ae> | %s'

# Firma valida per ogni commit (G = good)
for c in $(git rev-list -6 HEAD); do
  printf '%s  ' "$(git log -1 --format='%h %an' $c)"
  git log -1 --format='%G? %GS' $c
done

# Equità: conteggio commit per autore
git log -6 --format='%an' | sort | uniq -c
```

Atteso: date in ordine crescente, ogni riga firma `G`, conteggio identico per i 3 autori.

---

## Push

```bash
git push origin main
```

Una sola push via `origin` (matteo). Gli autori restano quelli impostati da `git as`;
su GitHub i commit appaiono **Verified** perché ogni firma usa la signing key
dell'account corrispondente.

Se serve disaccoppiare (push da un account diverso dall'autore) l'utente lo dirà
esplicitamente; di default non farlo.

---

## Se qualcosa va storto

- **Commit di matteo falliti / autori sballati / file inglobati nel commit sbagliato:**
  se **non hai ancora pushato**, annulla i commit mantenendo i file nel working tree e
  rifai da capo dopo aver caricato la chiave:
  ```bash
  git reset <ultimo-commit-buono-prima-dei-tuoi>   # es. git reset 4059c14
  git status -s                                     # i file tornano untracked
  ```
  Poi riparti dalla sezione _Esecuzione_. **Non** fare `reset --hard` (perderesti i file).
- **`Permission denied (publickey)` in push:** l'account non ha write access su quel
  repo → segnalalo, non forzare.
- **Badge `Unverified` su GitHub:** email autore non verificata sull'account, oppure
  signing key non caricata su GitHub. Mostra quale dei due, non rifirmare con un altro
  account per aggirare.

---

## Riassunto del flusso (checklist)

1. Fissa: file+ordine logico, account+ripartizione equa, intervallo date, repo/remote.
2. Pre-flight: `git remote -v`, `command -v git-as`, `ssh-add -l`.
3. Se manca la chiave di matteo nell'agent → chiedi `! ssh-add ~/.ssh/id_ed25519`.
4. Esegui i commit con `commit_at` (date crescenti, un file ciascuno, exit-code check).
5. Verifica: log, firme `G`, equità.
6. `git push origin main`.
