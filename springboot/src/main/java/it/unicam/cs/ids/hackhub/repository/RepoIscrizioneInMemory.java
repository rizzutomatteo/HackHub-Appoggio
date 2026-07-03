package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.entity.Team;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoIscrizione.
// Nessuna chiave naturale: id = UUID generato in salva(). Confronti per chiave naturale
// (Team.getNome(), Hackathon.getNome()), mai con equals()/==.
@Repository
public class RepoIscrizioneInMemory implements RepoIscrizione {

    // chiave = UUID generato in salva()
    private final Map<String, Iscrizione> storage = new ConcurrentHashMap<>();

    @Override
    public boolean esisteIscrizione(Team team, Hackathon hackathon) {
        return trova(team, hackathon) != null;
    }

    @Override
    public void salva(Iscrizione i) {
        storage.put(UUID.randomUUID().toString(), i);
    }

    @Override
    public List<Iscrizione> iscrizioniDi(Team team) {
        List<Iscrizione> risultato = new ArrayList<>();
        if (team == null) {
            return risultato;
        }
        for (Iscrizione i : storage.values()) {
            if (i.getTeam().getNome().equals(team.getNome())) {
                risultato.add(i);
            }
        }
        return risultato;
    }

    @Override
    public boolean iscrizioneInCorso(Team team, Hackathon hackathon) {
        // Il team e' iscritto all'hackathon e l'hackathon consente le sottomissioni ("in corso").
        Iscrizione i = trova(team, hackathon);
        return i != null && hackathon.sottomissioneConsentita();
    }

    // --- Helper per i REST controller ---

    // Iscrizione per (team, hackathon) confrontando le chiavi naturali; null se assente.
    public Iscrizione trova(Team team, Hackathon hackathon) {
        if (team == null || hackathon == null) {
            return null;
        }
        for (Iscrizione i : storage.values()) {
            if (i.getTeam().getNome().equals(team.getNome())
                    && i.getHackathon().getNome().equals(hackathon.getNome())) {
                return i;
            }
        }
        return null;
    }

    // id = UUID. Ritorna null se assente.
    public Iscrizione findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public List<Iscrizione> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id (UUID) di questa istanza, per identita' (==); null se non presente.
    public String idOf(Iscrizione i) {
        if (i == null) {
            return null;
        }
        for (Map.Entry<String, Iscrizione> e : storage.entrySet()) {
            if (e.getValue() == i) {
                return e.getKey();
            }
        }
        return null;
    }
}
