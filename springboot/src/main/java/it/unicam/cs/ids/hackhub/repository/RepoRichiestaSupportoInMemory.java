package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Incarico;
import it.unicam.cs.ids.hackhub.entity.RichiestaSupporto;
import it.unicam.cs.ids.hackhub.entity.RuoloStaff;
import it.unicam.cs.ids.hackhub.entity.StatoRichiesta;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoRichiestaSupporto.
// Nessuna chiave naturale: id = UUID generato in salva(). Confronti per chiave naturale
// (Team.getNome(), Hackathon.getNome(), Utente.getEmail()), mai con equals()/==.
@Repository
public class RepoRichiestaSupportoInMemory implements RepoRichiestaSupporto {

    // chiave = UUID generato in salva()
    private final Map<String, RichiestaSupporto> storage = new ConcurrentHashMap<>();

    @Override
    public boolean esisteRichiestaAperta(Team team, Hackathon hackathon) {
        return trovaAperta(team, hackathon) != null;
    }

    @Override
    public List<RichiestaSupporto> trovaPerHackathonDelMentore(Utente mentore) {
        // Scope via Incarico: le richieste degli hackathon in cui il mentore ha un Incarico ruolo Mentore.
        List<RichiestaSupporto> risultato = new ArrayList<>();
        if (mentore == null) {
            return risultato;
        }
        for (RichiestaSupporto r : storage.values()) {
            if (haIncarico(r.getHackathon(), mentore, RuoloStaff.Mentore)) {
                risultato.add(r);
            }
        }
        return risultato;
    }

    @Override
    public void salva(RichiestaSupporto r) {
        storage.put(UUID.randomUUID().toString(), r);
    }

    // --- Helper per i REST controller ---

    // Richiesta "Aperta" per (team, hackathon) confrontando le chiavi naturali; null se assente.
    public RichiestaSupporto trovaAperta(Team team, Hackathon hackathon) {
        if (team == null || hackathon == null) {
            return null;
        }
        for (RichiestaSupporto r : storage.values()) {
            if (r.getStato() == StatoRichiesta.Aperta
                    && r.getTeam().getNome().equals(team.getNome())
                    && r.getHackathon().getNome().equals(hackathon.getNome())) {
                return r;
            }
        }
        return null;
    }

    // id = UUID. Ritorna null se assente.
    public RichiestaSupporto findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public List<RichiestaSupporto> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id (UUID) di questa istanza, per identita' (==); null se non presente.
    public String idOf(RichiestaSupporto r) {
        if (r == null) {
            return null;
        }
        for (Map.Entry<String, RichiestaSupporto> e : storage.entrySet()) {
            if (e.getValue() == r) {
                return e.getKey();
            }
        }
        return null;
    }

    // L'utente ha un Incarico del ruolo dato nell'hackathon (confronto per email).
    private boolean haIncarico(Hackathon hackathon, Utente utente, RuoloStaff ruolo) {
        for (Incarico inc : hackathon.getStaff()) {
            if (inc.getRuolo() == ruolo
                    && inc.getUtente().getEmail().equals(utente.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
