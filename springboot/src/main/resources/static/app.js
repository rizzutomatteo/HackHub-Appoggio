// Client demo di HackHub - vanilla JS con fetch.
// baseUrl vuoto = stesso host che serve questa pagina (http://localhost:8080).
const baseUrl = "";

// Stato della demo: valori generati una volta, riusati fra i passi.
// Usiamo un suffisso random cosi' si puo' rieseguire la demo senza conflitti di id.
const suffisso = Math.floor(Math.random() * 100000);
const stato = {
  organizzatoreEmail: `org${suffisso}@hackhub.test`,
  membroEmail: `membro${suffisso}@hackhub.test`,
  hackathon: `HackDemo${suffisso}`,
  team: `Team${suffisso}`,
  richiestaId: null, // popolato dal passo "richiedi supporto"
};

const out = () => document.getElementById("out");

// Aggiorna la riga di stato in alto (email/nomi correnti della demo).
function aggiornaStato() {
  document.getElementById("stato").textContent =
    `org=${stato.organizzatoreEmail} | membro=${stato.membroEmail} | ` +
    `hackathon=${stato.hackathon} | team=${stato.team} | richiestaId=${stato.richiestaId ?? "-"}`;
}
aggiornaStato();

// Stampa un blocco leggibile nel <pre id="out">: etichetta + JSON (o testo) della risposta.
function mostra(etichetta, stato, corpo) {
  const testo =
    typeof corpo === "string" ? corpo : JSON.stringify(corpo, null, 2);
  out().textContent =
    `--- ${etichetta}  [HTTP ${stato}] ---\n${testo}\n\n` + out().textContent;
}

// Helper generico per le chiamate: fa il fetch, prova a leggere JSON e lo mostra in #out.
async function chiama(etichetta, metodo, percorso, corpo) {
  try {
    const opzioni = {
      method: metodo,
      headers: { "Content-Type": "application/json" },
    };
    if (corpo !== undefined) opzioni.body = JSON.stringify(corpo);

    const risposta = await fetch(baseUrl + percorso, opzioni);
    const testo = await risposta.text();
    let dati;
    try {
      dati = JSON.parse(testo);
    } catch {
      dati = testo;
    }
    mostra(etichetta, risposta.status, dati);
    return { ok: risposta.ok, dati };
  } catch (err) {
    mostra(etichetta, "ERRORE DI RETE", String(err));
    return { ok: false, dati: null };
  }
}

// --- Passi del flusso end-to-end -------------------------------------------

// 1. Registra 2 utenti: un organizzatore e un membro del team.
async function registraUtenti() {
  await chiama("Registra organizzatore", "POST", "/api/account/registrati", {
    nome: "Organizzatore Demo",
    email: stato.organizzatoreEmail,
    password: "password123",
  });
  await chiama("Registra membro", "POST", "/api/account/registrati", {
    nome: "Membro Demo",
    email: stato.membroEmail,
    password: "password123",
  });
  aggiornaStato();
}

// 2. Crea un hackathon (date in formato ISO yyyy-MM-dd).
async function creaHackathon() {
  return chiama("Crea hackathon", "POST", "/api/hackathon", {
    nome: stato.hackathon,
    dataInizio: "2026-09-01",
    dataFine: "2026-09-03",
    luogo: "Camerino",
    regolamento: "Regolamento demo: massimo fair play.",
    scadenzaIscrizioni: "2026-08-25",
    dimensioneMaxTeam: 4,
    montepremi: 1000,
    organizzatoreEmail: stato.organizzatoreEmail,
    giudiceEmail: stato.organizzatoreEmail,
    mentoriEmails: [stato.membroEmail],
  });
}

// 3. Crea un team (creatore = il membro registrato al passo 1).
async function creaTeam() {
  return chiama("Crea team", "POST", "/api/team", {
    nome: stato.team,
    creatoreEmail: stato.membroEmail,
  });
}

// 4. Iscrivi il team all'hackathon.
async function iscriviTeam() {
  return chiama(
    "Iscrivi team",
    "POST",
    `/api/team/${encodeURIComponent(stato.team)}/iscrivi`,
    {
      hackathon: stato.hackathon,
    },
  );
}

// 5. Richiedi supporto: il team apre una richiesta di mentoring.
//    Salviamo l'id restituito per proporre poi la call.
async function richiediSupporto() {
  const r = await chiama(
    "Richiedi supporto",
    "POST",
    "/api/mentoring/richieste",
    {
      teamNome: stato.team,
      hackathonNome: stato.hackathon,
      messaggio: "Ci serve aiuto sull'architettura.",
    },
  );
  if (r.ok && r.dati && r.dati.id !== undefined) {
    stato.richiestaId = r.dati.id;
    aggiornaStato();
  }
  return r;
}

// 6. Proponi una call: il mentore propone uno slot in risposta alla richiesta.
//    slot in formato ISO datetime.
async function proponiCall() {
  if (stato.richiestaId === null) {
    mostra(
      "Proponi call",
      "SALTATO",
      "Esegui prima il passo 5 (richiedi supporto) per avere una richiestaId.",
    );
    return;
  }
  return chiama("Proponi call", "POST", "/api/mentoring/call", {
    mentoreEmail: stato.membroEmail,
    richiestaId: stato.richiestaId,
    slot: "2026-09-02T15:00:00",
  });
}

// 7. Paga il montepremi dell'hackathon (integrazione Adapter -> Stripe test / fallback).
async function pagaMontepremi() {
  return chiama(
    "Paga montepremi",
    "POST",
    `/api/hackathon/${encodeURIComponent(stato.hackathon)}/paga`,
  );
}

// Extra: elenco degli hackathon pubblici.
async function hackathonPubblici() {
  return chiama("Hackathon pubblici", "GET", "/api/hackathon/pubblici");
}

// Esegue in sequenza i passi 1 -> 7.
async function eseguiTutto() {
  await registraUtenti();
  await creaHackathon();
  await creaTeam();
  await iscriviTeam();
  await richiediSupporto();
  await proponiCall();
  await pagaMontepremi();
  mostra(
    "Flusso completo",
    "FINE",
    "Sequenza 1 -> 7 eseguita. Scorri sotto per i dettagli di ogni passo.",
  );
}

function pulisci() {
  out().textContent = "Output pulito.";
}
