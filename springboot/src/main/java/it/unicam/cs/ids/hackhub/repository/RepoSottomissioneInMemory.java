package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoSottomissione.
// Nessuna chiave naturale: id = UUID generato in salva(). Confronti per chiave naturale
// dell'hackathon collegato via Iscrizione (Hackathon.getNome()), mai con equals()/==.
@Repository
public class RepoSottomissioneInMemory implements RepoSottomissione {

    // chiave = UUID generato in salva()
    private final Map<String, Sottomissione> storage = new ConcurrentHashMap<>();

    @Override
    public void salva(Sottomissione sub) {
        storage.put(UUID.randomUUID().toString(), sub);
    }

    @Override
    public List<Sottomissione> sottomissioniDi(Hackathon h) {
        List<Sottomissione> risultato = new ArrayList<>();
        if (h == null) {
            return risultato;
        }
        for (Sottomissione sub : storage.values()) {
            if (sub.getIscrizione().getHackathon().getNome().equals(h.getNome())) {
                risultato.add(sub);
            }
        }
        return risultato;
    }

    // --- Helper per i REST controller ---

    // id = UUID. Ritorna null se assente.
    public Sottomissione findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public List<Sottomissione> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id (UUID) di questa istanza, per identita' (==); null se non presente.
    public String idOf(Sottomissione sub) {
        if (sub == null) {
            return null;
        }
        for (Map.Entry<String, Sottomissione> e : storage.entrySet()) {
            if (e.getValue() == sub) {
                return e.getKey();
            }
        }
        return null;
    }
}
