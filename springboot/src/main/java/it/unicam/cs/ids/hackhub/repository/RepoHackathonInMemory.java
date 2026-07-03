package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoHackathon.
// Chiave naturale: il nome dell'hackathon (Hackathon.getNome()). L'id di dominio coincide con il nome.
@Repository
public class RepoHackathonInMemory implements RepoHackathon {

    // chiave = nome dell'hackathon
    private final Map<String, Hackathon> storage = new ConcurrentHashMap<>();

    @Override
    public void salva(Hackathon h) {
        storage.put(h.getNome(), h);
    }

    @Override
    public List<Hackathon> tutti() {
        return new ArrayList<>(storage.values());
    }

    // --- Helper per i REST controller ---

    // id = chiave naturale (nome). Ritorna null se assente.
    public Hackathon findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public Hackathon trovaPerNome(String nome) {
        return findById(nome);
    }

    public List<Hackathon> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id di questa istanza = chiave naturale (nome), o null.
    public String idOf(Hackathon h) {
        return h == null ? null : h.getNome();
    }
}
