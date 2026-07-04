package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Team;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoTeam.
// Chiave naturale: il nome del team (Team.getNome()). L'id di dominio coincide con il nome.
@Repository
public class RepoTeamInMemory implements RepoTeam {

    // chiave = nome del team
    private final Map<String, Team> storage = new ConcurrentHashMap<>();

    @Override
    public boolean esisteNome(String nome) {
        return storage.containsKey(nome);
    }

    @Override
    public void salva(Team t) {
        storage.put(t.getNome(), t);
    }

    // --- Helper per i REST controller ---

    // id = chiave naturale (nome). Ritorna null se assente.
    public Team findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public Team trovaPerNome(String nome) {
        return findById(nome);
    }

    public List<Team> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id di questa istanza = chiave naturale (nome), o null.
    public String idOf(Team t) {
        return t == null ? null : t.getNome();
    }
}
