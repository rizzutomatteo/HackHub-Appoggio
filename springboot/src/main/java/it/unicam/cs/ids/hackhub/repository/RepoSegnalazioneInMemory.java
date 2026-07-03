package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Incarico;
import it.unicam.cs.ids.hackhub.entity.RuoloStaff;
import it.unicam.cs.ids.hackhub.entity.Segnalazione;
import it.unicam.cs.ids.hackhub.entity.StatoSegnalazione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoSegnalazione.
// Nessuna chiave naturale: id = UUID generato in salva(). Confronti per chiave naturale
// (Utente.getEmail(), Team.getNome(), Hackathon.getNome()), mai con equals()/==.
@Repository
public class RepoSegnalazioneInMemory implements RepoSegnalazione {

    // chiave = UUID generato in salva()
    private final Map<String, Segnalazione> storage = new ConcurrentHashMap<>();

    @Override
    public boolean esisteSegnalazioneAperta(Utente mentore, Team team, Hackathon hackathon) {
        return trovaAperta(mentore, team, hackathon) != null;
    }

    @Override
    public List<Segnalazione> trovaPerHackathonDellOrganizzatore(Utente organizzatore) {
        // Scope via Incarico: le segnalazioni degli hackathon in cui l'organizzatore ha ruolo Organizzatore.
        List<Segnalazione> risultato = new ArrayList<>();
        if (organizzatore == null) {
            return risultato;
        }
        for (Segnalazione seg : storage.values()) {
            if (haIncarico(seg.getHackathon(), organizzatore, RuoloStaff.Organizzatore)) {
                risultato.add(seg);
            }
        }
        return risultato;
    }

    @Override
    public void salva(Segnalazione seg) {
        storage.put(UUID.randomUUID().toString(), seg);
    }

    // --- Helper per i REST controller ---

    // Segnalazione "Aperta" per (mentore, team, hackathon) confrontando le chiavi naturali; null se assente.
    public Segnalazione trovaAperta(Utente mentore, Team team, Hackathon hackathon) {
        if (mentore == null || team == null || hackathon == null) {
            return null;
        }
        for (Segnalazione seg : storage.values()) {
            if (seg.getStato() == StatoSegnalazione.Aperta
                    && seg.getSegnalante().getEmail().equals(mentore.getEmail())
                    && seg.getTeam().getNome().equals(team.getNome())
                    && seg.getHackathon().getNome().equals(hackathon.getNome())) {
                return seg;
            }
        }
        return null;
    }

    // id = UUID. Ritorna null se assente.
    public Segnalazione findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public List<Segnalazione> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id (UUID) di questa istanza, per identita' (==); null se non presente.
    public String idOf(Segnalazione seg) {
        if (seg == null) {
            return null;
        }
        for (Map.Entry<String, Segnalazione> e : storage.entrySet()) {
            if (e.getValue() == seg) {
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
