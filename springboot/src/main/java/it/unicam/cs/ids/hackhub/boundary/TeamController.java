package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Invito;
import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.entity.StatoInvito;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoHackathonInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoInvitoInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoIscrizioneInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoTeamInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoUtenteInMemory;
import it.unicam.cs.ids.hackhub.service.ServiceInvito;
import it.unicam.cs.ids.hackhub.service.ServiceIscrizione;
import it.unicam.cs.ids.hackhub.service.ServiceTeam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Controller REST di boundary per Team, iscrizioni e inviti.
// Convenzioni: create -> 201 con Map.of("id", ...); esito bool false -> 409;
// entita' mancante -> 404; input mancante -> 400. Id: Team=nome; le entita' senza
// chiave naturale (Iscrizione, Invito) usano l'id UUID via repo.idOf(...).
@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final ServiceTeam serviceTeam;
    private final ServiceIscrizione serviceIscrizione;
    private final ServiceInvito serviceInvito;
    private final RepoTeamInMemory repoTeam;
    private final RepoHackathonInMemory repoHackathon;
    private final RepoUtenteInMemory repoUtente;
    private final RepoIscrizioneInMemory repoIscrizione;
    private final RepoInvitoInMemory repoInvito;

    public TeamController(ServiceTeam serviceTeam,
                         ServiceIscrizione serviceIscrizione,
                         ServiceInvito serviceInvito,
                         RepoTeamInMemory repoTeam,
                         RepoHackathonInMemory repoHackathon,
                         RepoUtenteInMemory repoUtente,
                         RepoIscrizioneInMemory repoIscrizione,
                         RepoInvitoInMemory repoInvito) {
        this.serviceTeam = serviceTeam;
        this.serviceIscrizione = serviceIscrizione;
        this.serviceInvito = serviceInvito;
        this.repoTeam = repoTeam;
        this.repoHackathon = repoHackathon;
        this.repoUtente = repoUtente;
        this.repoIscrizione = repoIscrizione;
        this.repoInvito = repoInvito;
    }

    // --- DTO di richiesta (record annidati) ---

    public record CreaTeamRequest(String nome, String creatoreEmail) {}

    public record IscriviRequest(String hackathon) {}

    public record InvitoRequest(String utenteInvitatoEmail) {}

    // --- Endpoint ---

    // POST /api/team -> crea un team.
    // CAVEAT: ServiceTeam e' un flusso a due passi *stateful* (iniziaCreazioneTeam salva
    // il creatore corrente in un campo di istanza, creaTeam lo consuma). Non e' thread-safe:
    // due richieste concorrenti potrebbero sovrascriversi il creatoreCorrente. Accettabile
    // solo per la demo in-memory a singolo client; in produzione andrebbe reso stateless.
    @PostMapping
    public ResponseEntity<?> crea(@RequestBody CreaTeamRequest req) {
        if (req == null || req.nome() == null || req.creatoreEmail() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Utente creatore = repoUtente.trovaPerEmail(req.creatoreEmail());
        if (creatore == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "creatore non trovato"));
        }
        // Passo 1 poi passo 2 del flusso stateful.
        serviceTeam.iniziaCreazioneTeam(creatore);
        if (serviceTeam.creaTeam(req.nome())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", req.nome()));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("errore", "creazione team non riuscita"));
    }

    // POST /api/team/{nome}/iscrivi -> iscrive il team a un hackathon.
    @PostMapping("/{nome}/iscrivi")
    public ResponseEntity<?> iscrivi(@PathVariable String nome, @RequestBody IscriviRequest req) {
        if (req == null || req.hackathon() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Team team = repoTeam.trovaPerNome(nome);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "team non trovato"));
        }
        Hackathon hackathon = repoHackathon.trovaPerNome(req.hackathon());
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "hackathon non trovato"));
        }
        if (serviceIscrizione.iscriviTeam(team, hackathon)) {
            Iscrizione iscr = repoIscrizione.trova(team, hackathon);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("iscrizioneId", repoIscrizione.idOf(iscr)));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("errore", "iscrizione non riuscita"));
    }

    // POST /api/team/{nome}/inviti -> invita un utente nel team.
    @PostMapping("/{nome}/inviti")
    public ResponseEntity<?> invita(@PathVariable String nome, @RequestBody InvitoRequest req) {
        if (req == null || req.utenteInvitatoEmail() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Team team = repoTeam.trovaPerNome(nome);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "team non trovato"));
        }
        Utente utente = repoUtente.trovaPerEmail(req.utenteInvitatoEmail());
        if (utente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "utente non trovato"));
        }
        if (serviceInvito.invita(team, utente)) {
            Invito creato = trovaInvitoCreato(team, utente);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("invitoId", repoInvito.idOf(creato)));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("errore", "invito non riuscito"));
    }

    // POST /api/team/inviti/{id}/accetta -> accetta un invito pendente.
    @PostMapping("/inviti/{id}/accetta")
    public ResponseEntity<?> accetta(@PathVariable String id) {
        Invito invito = repoInvito.findById(id);
        if (invito == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "invito non trovato"));
        }
        if (serviceInvito.accetta(invito)) {
            return ResponseEntity.ok(Map.of("id", id));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("errore", "accettazione non riuscita"));
    }

    // Individua l'Invito appena creato scorrendo tutti gli inviti e filtrando per team
    // (Invito.getTeam()), utente invitato e stato Pendente. Si prende l'ultimo che combacia,
    // che e' quello appena salvato da ServiceInvito.invita(...).
    private Invito trovaInvitoCreato(Team team, Utente utente) {
        Invito ultimo = null;
        for (Invito inv : repoInvito.findAll()) {
            if (inv.getStato() == StatoInvito.Pendente
                    && inv.getTeam().getNome().equals(team.getNome())
                    && inv.getUtenteInvitato().getEmail().equals(utente.getEmail())) {
                ultimo = inv;
            }
        }
        return ultimo;
    }
}
