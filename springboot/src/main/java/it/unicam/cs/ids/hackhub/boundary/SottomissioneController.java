package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import it.unicam.cs.ids.hackhub.repository.RepoHackathonInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoIscrizioneInMemory;
import it.unicam.cs.ids.hackhub.repository.RepoSottomissioneInMemory;
import it.unicam.cs.ids.hackhub.service.ServiceSottomissione;
import it.unicam.cs.ids.hackhub.service.ServiceValutazione;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// Controller REST di boundary per sottomissioni e valutazioni.
// Convenzioni: create -> 201 con Map.of("id", ...); esito bool true -> 200 / false -> 409;
// entita' mancante -> 404; input mancante -> 400; List -> 200. La Sottomissione non ha
// chiave naturale: id = UUID via repoSottomissione.idOf(...).
@RestController
@RequestMapping("/api/sottomissioni")
public class SottomissioneController {

    private final ServiceSottomissione serviceSottomissione;
    private final ServiceValutazione serviceValutazione;
    private final RepoIscrizioneInMemory repoIscrizione;
    private final RepoSottomissioneInMemory repoSottomissione;
    private final RepoHackathonInMemory repoHackathon;

    public SottomissioneController(ServiceSottomissione serviceSottomissione,
                                   ServiceValutazione serviceValutazione,
                                   RepoIscrizioneInMemory repoIscrizione,
                                   RepoSottomissioneInMemory repoSottomissione,
                                   RepoHackathonInMemory repoHackathon) {
        this.serviceSottomissione = serviceSottomissione;
        this.serviceValutazione = serviceValutazione;
        this.repoIscrizione = repoIscrizione;
        this.repoSottomissione = repoSottomissione;
        this.repoHackathon = repoHackathon;
    }

    // --- DTO di richiesta (record annidati) ---

    public record SottomissioneRequest(String iscrizioneId, String titolo, String contenuto) {}

    public record ValutazioneRequest(String giudizio, int punteggio) {}

    // --- Endpoint ---

    // POST /api/sottomissioni -> invia una sottomissione per una data iscrizione.
    @PostMapping
    public ResponseEntity<?> invia(@RequestBody SottomissioneRequest req) {
        if (req == null || req.iscrizioneId() == null
                || req.titolo() == null || req.contenuto() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Iscrizione iscr = repoIscrizione.findById(req.iscrizioneId());
        if (iscr == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "iscrizione non trovata"));
        }
        if (serviceSottomissione.inviaSottomissione(iscr, req.titolo(), req.contenuto())) {
            // La Sottomissione creata e' quella ora contenuta nell'Iscrizione.
            Sottomissione creata = iscr.getSottomissione();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("sottomissioneId", repoSottomissione.idOf(creata)));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("errore", "sottomissione non riuscita"));
    }

    // GET /api/sottomissioni/hackathon/{nome} -> elenca le sottomissioni di un hackathon.
    @GetMapping("/hackathon/{nome}")
    public ResponseEntity<?> elenco(@PathVariable String nome) {
        Hackathon hackathon = repoHackathon.trovaPerNome(nome);
        if (hackathon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "hackathon non trovato"));
        }
        List<Sottomissione> sottomissioni = serviceValutazione.elencoSottomissioni(hackathon);
        return ResponseEntity.ok(sottomissioni);
    }

    // POST /api/sottomissioni/{id}/valuta -> valuta una sottomissione.
    @PostMapping("/{id}/valuta")
    public ResponseEntity<?> valuta(@PathVariable String id, @RequestBody ValutazioneRequest req) {
        if (req == null || req.giudizio() == null) {
            return ResponseEntity.badRequest().body(Map.of("errore", "input mancante"));
        }
        Sottomissione sub = repoSottomissione.findById(id);
        if (sub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errore", "sottomissione non trovata"));
        }
        if (serviceValutazione.valuta(sub, req.giudizio(), req.punteggio())) {
            return ResponseEntity.ok(Map.of("id", id));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("errore", "valutazione non riuscita"));
    }
}
