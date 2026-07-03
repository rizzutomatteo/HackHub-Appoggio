package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Invito;
import it.unicam.cs.ids.hackhub.entity.StatoInvito;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoInvito.
// Nessuna chiave naturale: id = UUID generato in salva(). Confronti per chiave naturale
// delle entita' collegate (Team.getNome(), Utente.getEmail()), mai con equals()/==.
@Repository
public class RepoInvitoInMemory implements RepoInvito {

    // chiave = UUID generato in salva()
    private final Map<String, Invito> storage = new ConcurrentHashMap<>();

    @Override
    public boolean esisteInvitoPendente(Team team, Utente u) {
        if (team == null || u == null) {
            return false;
        }
        for (Invito inv : storage.values()) {
            if (inv.getStato() == StatoInvito.Pendente
                    && inv.getTeam().getNome().equals(team.getNome())
                    && inv.getUtenteInvitato().getEmail().equals(u.getEmail())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void salva(Invito inv) {
        storage.put(UUID.randomUUID().toString(), inv);
    }

    // --- Helper per i REST controller ---

    // id = UUID. Ritorna null se assente.
    public Invito findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public List<Invito> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id (UUID) di questa istanza, per identita' (==); null se non presente.
    public String idOf(Invito inv) {
        if (inv == null) {
            return null;
        }
        for (Map.Entry<String, Invito> e : storage.entrySet()) {
            if (e.getValue() == inv) {
                return e.getKey();
            }
        }
        return null;
    }
}
