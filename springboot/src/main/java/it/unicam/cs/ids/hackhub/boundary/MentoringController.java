package it.unicam.cs.ids.hackhub.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.PropostaCall;
import it.unicam.cs.ids.hackhub.entity.RichiestaSupporto;
import it.unicam.cs.ids.hackhub.entity.Segnalazione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoHackathonInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoPropostaCallInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoRichiestaSupportoInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoSegnalazioneInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoTeamInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoUtenteInMemory;
import it.unicam.cs.ids.hackhub.service.ServiceCall;
import it.unicam.cs.ids.hackhub.service.ServiceSegnalazione;
import it.unicam.cs.ids.hackhub.service.ServiceSupporto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Boundary (REST) del mentoring: espone i UC di supporto (UC11/UC12), call (UC13-UC15)
// e governance (UC17/UC18). Risolve gli input alle entita' tramite gli helper dei repo
// InMemory (chiavi naturali: nome team/hackathon, email utente) e delega ai Service.
// L'endpoint POST /call attiva l'integrazione Google Calendar tramite l'adapter (via ServiceCall).
@RestController
@RequestMapping("/api/mentoring")
public class MentoringController {

    private final ServiceSupporto serviceSupporto;
    private final ServiceCall serviceCall;
    private final ServiceSegnalazione serviceSegnalazione;
    private final RepoTeamInMemory repoTeam;
    private final RepoHackathonInMemory repoHackathon;
    private final RepoUtenteInMemory repoUtente;
    private final RepoRichiestaSupportoInMemory repoRichiesta;
    private final RepoPropostaCallInMemory repoProposta;
    private final RepoSegnalazioneInMemory repoSegnalazione;

    public MentoringController(ServiceSupporto serviceSupporto,
                              ServiceCall serviceCall,
                              ServiceSegnalazione serviceSegnalazione,
                              RepoTeamInMemory repoTeam,
                              RepoHackathonInMemory repoHackathon,
                              RepoUtenteInMemory repoUtente,
                              RepoRichiestaSupportoInMemory repoRichiesta,
                              RepoPropostaCallInMemory repoProposta,
                              RepoSegnalazioneInMemory repoSegnalazione) {
        this.serviceSupporto = serviceSupporto;
        this.serviceCall = serviceCall;
        this.serviceSegnalazione = serviceSegnalazione;
        this.repoTeam = repoTeam;
        this.repoHackathon = repoHackathon;
        this.repoUtente = repoUtente;
        this.repoRichiesta = repoRichiesta;
        this.repoProposta = repoProposta;
        this.repoSegnalazione = repoSegnalazione;
    }

    // --- DTO di richiesta (record annidati) ---

    public record RichiestaBody(String teamNome, String hackathonNome, String messaggio) {}

    public record CallBody(String mentoreEmail, String richiestaId, String slot) {}

    public record SegnalazioneBody(String mentoreEmail, String teamNome, String hackathonNome, String descrizione) {}

    public record DecisioneBody(String decisione) {}

    // --- UC11: Richiede Supporto ---

    @PostMapping("/richieste")
    public ResponseEntity<?> richiediSupporto(@RequestBody RichiestaBody body) {
        if (body == null || body.teamNome() == null || body.hackathonNome() == null || body.messaggio() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Team team = repoTeam.trovaPerNome(body.teamNome());
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "team non trovato"));
        }
        Hackathon hackathon = repoHackathon.trovaPerNome(body.hackathonNome());
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "hackathon non trovato"));
        }

        boolean esito = serviceSupporto.richiediSupporto(team, hackathon, body.messaggio());
        if (!esito) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("errore", "richiesta di supporto non consentita"));
        }

        RichiestaSupporto richiesta = repoRichiesta.trovaAperta(team, hackathon);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("richiestaId", repoRichiesta.idOf(richiesta)));
    }

    // --- UC12: Visualizza Richieste del Mentore ---

    @GetMapping("/richieste/mentore/{email}")
    public ResponseEntity<?> richiesteDelMentore(@PathVariable String email) {
        Utente mentore = repoUtente.trovaPerEmail(email);
        if (mentore == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "mentore non trovato"));
        }
        List<RichiestaSupporto> richieste = serviceSupporto.richiesteDelMentore(mentore);
        return ResponseEntity.ok(richieste);
    }

    // --- UC13: Propone Call (attiva Google Calendar via adapter) ---

    @PostMapping("/call")
    public ResponseEntity<?> proponiCall(@RequestBody CallBody body) {
        if (body == null || body.mentoreEmail() == null || body.richiestaId() == null || body.slot() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Utente mentore = repoUtente.trovaPerEmail(body.mentoreEmail());
        if (mentore == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "mentore non trovato"));
        }
        RichiestaSupporto richiesta = repoRichiesta.findById(body.richiestaId());
        if (richiesta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "richiesta non trovata"));
        }

        boolean esito = serviceCall.proponiCall(mentore, richiesta, LocalDateTime.parse(body.slot()));
        if (!esito) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("errore", "proposta di call non consentita"));
        }

        List<PropostaCall> proposte = repoProposta.propostePerTeam(richiesta.getTeam());
        PropostaCall proposta = proposte.get(proposte.size() - 1);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("propostaId", repoProposta.idOf(proposta)));
    }

    // --- UC14: Accetta Proposta (conferma Calendar via adapter) ---

    @PostMapping("/call/{id}/accetta")
    public ResponseEntity<?> accettaProposta(@PathVariable String id) {
        PropostaCall proposta = repoProposta.findById(id);
        if (proposta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "proposta non trovata"));
        }
        boolean esito = serviceCall.accettaProposta(proposta);
        if (!esito) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("errore", "accettazione non consentita"));
        }
        return ResponseEntity.ok().build();
    }

    // --- UC15: Rifiuta Proposta ---

    @PostMapping("/call/{id}/rifiuta")
    public ResponseEntity<?> rifiutaProposta(@PathVariable String id) {
        PropostaCall proposta = repoProposta.findById(id);
        if (proposta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "proposta non trovata"));
        }
        boolean esito = serviceCall.rifiutaProposta(proposta);
        if (!esito) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("errore", "rifiuto non consentito"));
        }
        return ResponseEntity.ok().build();
    }

    // --- UC17: Segnala Team ---

    @PostMapping("/segnalazioni")
    public ResponseEntity<?> segnala(@RequestBody SegnalazioneBody body) {
        if (body == null || body.mentoreEmail() == null || body.teamNome() == null
                || body.hackathonNome() == null || body.descrizione() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Utente mentore = repoUtente.trovaPerEmail(body.mentoreEmail());
        if (mentore == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "mentore non trovato"));
        }
        Team team = repoTeam.trovaPerNome(body.teamNome());
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "team non trovato"));
        }
        Hackathon hackathon = repoHackathon.trovaPerNome(body.hackathonNome());
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "hackathon non trovato"));
        }

        boolean esito = serviceSegnalazione.segnala(mentore, team, hackathon, body.descrizione());
        if (!esito) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("errore", "segnalazione non consentita"));
        }

        Segnalazione segnalazione = repoSegnalazione.trovaAperta(mentore, team, hackathon);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("segnalazioneId", repoSegnalazione.idOf(segnalazione)));
    }

    // --- UC18: Visualizza Segnalazioni dell'Organizzatore ---

    @GetMapping("/segnalazioni/organizzatore/{email}")
    public ResponseEntity<?> segnalazioniDellOrganizzatore(@PathVariable String email) {
        Utente organizzatore = repoUtente.trovaPerEmail(email);
        if (organizzatore == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "organizzatore non trovato"));
        }
        List<Segnalazione> segnalazioni = serviceSegnalazione.segnalazioniDellOrganizzatore(organizzatore);
        return ResponseEntity.ok(segnalazioni);
    }

    // --- UC18: Gestisci Segnalazione ---

    @PostMapping("/segnalazioni/{id}/gestisci")
    public ResponseEntity<?> gestisci(@PathVariable String id, @RequestBody DecisioneBody body) {
        if (body == null || body.decisione() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Segnalazione segnalazione = repoSegnalazione.findById(id);
        if (segnalazione == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "segnalazione non trovata"));
        }
        boolean esito = serviceSegnalazione.gestisci(segnalazione, body.decisione());
        if (!esito) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("errore", "gestione non consentita"));
        }
        return ResponseEntity.ok().build();
    }

    // --- UC18 3b: Archivia Segnalazione ---

    @PostMapping("/segnalazioni/{id}/archivia")
    public ResponseEntity<?> archivia(@PathVariable String id) {
        Segnalazione segnalazione = repoSegnalazione.findById(id);
        if (segnalazione == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errore", "segnalazione non trovata"));
        }
        boolean esito = serviceSegnalazione.archivia(segnalazione);
        if (!esito) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("errore", "archiviazione non consentita"));
        }
        return ResponseEntity.ok().build();
    }
}
