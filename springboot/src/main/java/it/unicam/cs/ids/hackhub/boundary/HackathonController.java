package it.unicam.cs.ids.hackhub.boundary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoHackathonInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoTeamInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoUtenteInMemory;
import it.unicam.cs.ids.hackhub.service.ServiceHackathon;
import it.unicam.cs.ids.hackhub.service.ServicePagamento;

// Boundary REST per il ciclo di vita dell'hackathon (creazione, avvio, valutazione,
// proclamazione, pagamento montepremi, gestione mentori). Delega la logica ai Service;
// usa le impl InMemory dei repository solo per risolvere le entita' referenziate.
@RestController
@RequestMapping("/api/hackathon")
public class HackathonController {

    private final ServiceHackathon serviceHackathon;
    private final ServicePagamento servicePagamento;
    private final RepoHackathonInMemory repoHackathon;
    private final RepoTeamInMemory repoTeam;
    private final RepoUtenteInMemory repoUtente;

    public HackathonController(ServiceHackathon serviceHackathon,
                              ServicePagamento servicePagamento,
                              RepoHackathonInMemory repoHackathon,
                              RepoTeamInMemory repoTeam,
                              RepoUtenteInMemory repoUtente) {
        this.serviceHackathon = serviceHackathon;
        this.servicePagamento = servicePagamento;
        this.repoHackathon = repoHackathon;
        this.repoTeam = repoTeam;
        this.repoUtente = repoUtente;
    }

    // Crea un hackathon: risolve organizzatore/giudice/mentori (404 se manca uno),
    // parsa le date ISO, delega a creaHackathon. L'id di dominio coincide con il nome.
    @PostMapping
    public ResponseEntity<?> crea(@RequestBody CreaHackathonRequest richiesta) {
        if (richiesta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errore", "corpo della richiesta mancante"));
        }

        Utente organizzatore = repoUtente.trovaPerEmail(richiesta.organizzatoreEmail());
        if (organizzatore == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "organizzatore non trovato"));
        }
        Utente giudice = repoUtente.trovaPerEmail(richiesta.giudiceEmail());
        if (giudice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "giudice non trovato"));
        }

        List<Utente> mentori = new ArrayList<>();
        if (richiesta.mentoriEmails() != null) {
            for (String email : richiesta.mentoriEmails()) {
                Utente mentore = repoUtente.trovaPerEmail(email);
                if (mentore == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("errore", "mentore non trovato: " + email));
                }
                mentori.add(mentore);
            }
        }

        boolean creato = serviceHackathon.creaHackathon(
                richiesta.nome(),
                LocalDate.parse(richiesta.dataInizio()),
                LocalDate.parse(richiesta.dataFine()),
                richiesta.luogo(),
                richiesta.regolamento(),
                LocalDate.parse(richiesta.scadenzaIscrizioni()),
                richiesta.dimensioneMaxTeam(),
                richiesta.montepremi(),
                organizzatore,
                giudice,
                mentori);

        if (!creato) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errore", "creazione non riuscita"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", richiesta.nome()));
    }

    // UC21: informazioni pubbliche di tutti gli hackathon.
    @GetMapping("/pubblici")
    public ResponseEntity<?> pubblici() {
        return ResponseEntity.ok(serviceHackathon.hackathonPubblici());
    }

    // Avvia l'hackathon (transizione di stato).
    @PostMapping("/{nome}/avvia")
    public ResponseEntity<?> avvia(@PathVariable String nome) {
        Hackathon hackathon = repoHackathon.trovaPerNome(nome);
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "hackathon non trovato"));
        }
        boolean avviato = serviceHackathon.avvia(hackathon);
        if (!avviato) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errore", "avvio non consentito"));
        }
        return ResponseEntity.ok(Map.of("id", nome));
    }

    // Avvia la fase di valutazione (transizione di stato).
    @PostMapping("/{nome}/inizia-valutazione")
    public ResponseEntity<?> iniziaValutazione(@PathVariable String nome) {
        Hackathon hackathon = repoHackathon.trovaPerNome(nome);
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "hackathon non trovato"));
        }
        boolean iniziata = serviceHackathon.iniziaFaseValutazione(hackathon);
        if (!iniziata) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errore", "inizio valutazione non consentito"));
        }
        return ResponseEntity.ok(Map.of("id", nome));
    }

    // Classifica per la proclamazione: lista ordinata delle sottomissioni, o 409 se non pronta.
    @GetMapping("/{nome}/classifica")
    public ResponseEntity<?> classifica(@PathVariable String nome) {
        Hackathon hackathon = repoHackathon.trovaPerNome(nome);
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "hackathon non trovato"));
        }
        List<Sottomissione> classifica = serviceHackathon.preparaProclamazione(hackathon);
        if (classifica == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errore", "classifica non disponibile"));
        }
        return ResponseEntity.ok(classifica);
    }

    // Proclama il team vincitore (Team identificato per nome).
    @PostMapping("/{nome}/proclama")
    public ResponseEntity<?> proclama(@PathVariable String nome, @RequestBody ProclamaRequest richiesta) {
        if (richiesta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errore", "corpo della richiesta mancante"));
        }
        Hackathon hackathon = repoHackathon.trovaPerNome(nome);
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "hackathon non trovato"));
        }
        Team team = repoTeam.trovaPerNome(richiesta.teamVincitore());
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "team non trovato"));
        }
        boolean proclamato = serviceHackathon.proclamaVincitore(hackathon, team);
        if (!proclamato) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errore", "proclamazione non consentita"));
        }
        return ResponseEntity.ok(Map.of("id", nome));
    }

    // UC16: eroga il montepremi al team vincitore (integrazione Stripe via PaymentGateway).
    @PostMapping("/{nome}/paga")
    public ResponseEntity<?> paga(@PathVariable String nome) {
        Hackathon hackathon = repoHackathon.trovaPerNome(nome);
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "hackathon non trovato"));
        }
        boolean pagato = servicePagamento.paga(hackathon);
        if (!pagato) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errore", "pagamento non riuscito"));
        }
        return ResponseEntity.ok(Map.of("esito", "montepremi erogato"));
    }

    // UC22: l'Organizzatore assegnato aggiunge un Mentore a un hackathon non concluso.
    @PostMapping("/{nome}/mentori")
    public ResponseEntity<?> aggiungiMentore(@PathVariable String nome, @RequestBody AggiungiMentoreRequest richiesta) {
        if (richiesta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errore", "corpo della richiesta mancante"));
        }
        Hackathon hackathon = repoHackathon.trovaPerNome(nome);
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "hackathon non trovato"));
        }
        Utente organizzatore = repoUtente.trovaPerEmail(richiesta.organizzatoreEmail());
        if (organizzatore == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "organizzatore non trovato"));
        }
        Utente utente = repoUtente.trovaPerEmail(richiesta.utenteEmail());
        if (utente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "utente non trovato"));
        }
        boolean aggiunto = serviceHackathon.aggiungiMentore(organizzatore, hackathon, utente);
        if (!aggiunto) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errore", "aggiunta mentore non consentita"));
        }
        return ResponseEntity.ok(Map.of("id", nome));
    }

    public record CreaHackathonRequest(String nome,
                                       String dataInizio,
                                       String dataFine,
                                       String luogo,
                                       String regolamento,
                                       String scadenzaIscrizioni,
                                       int dimensioneMaxTeam,
                                       long montepremi,
                                       String organizzatoreEmail,
                                       String giudiceEmail,
                                       List<String> mentoriEmails) {
    }

    public record ProclamaRequest(String teamVincitore) {
    }

    public record AggiungiMentoreRequest(String organizzatoreEmail, String utenteEmail) {
    }
}
