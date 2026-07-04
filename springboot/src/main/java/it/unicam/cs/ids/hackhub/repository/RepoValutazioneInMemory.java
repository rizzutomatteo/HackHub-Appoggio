package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Valutazione;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoValutazione.
// Nessuna chiave naturale: id = UUID generato in salva().
@Repository
public class RepoValutazioneInMemory implements RepoValutazione {

    // chiave = UUID generato in salva()
    private final Map<String, Valutazione> storage = new ConcurrentHashMap<>();

    @Override
    public void salva(Valutazione v) {
        storage.put(UUID.randomUUID().toString(), v);
    }

    // --- Helper per i REST controller ---

    // id = UUID. Ritorna null se assente.
    public Valutazione findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public List<Valutazione> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id (UUID) di questa istanza, per identita' (==); null se non presente.
    public String idOf(Valutazione v) {
        if (v == null) {
            return null;
        }
        for (Map.Entry<String, Valutazione> e : storage.entrySet()) {
            if (e.getValue() == v) {
                return e.getKey();
            }
        }
        return null;
    }
}
