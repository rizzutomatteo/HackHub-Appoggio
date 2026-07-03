package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.PropostaCall;
import it.unicam.cs.ids.hackhub.entity.Team;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoPropostaCall.
// Nessuna chiave naturale: id = UUID generato in salva(). Confronti per chiave naturale
// del destinatario (Team.getNome()), mai con equals()/==.
@Repository
public class RepoPropostaCallInMemory implements RepoPropostaCall {

    // chiave = UUID generato in salva()
    private final Map<String, PropostaCall> storage = new ConcurrentHashMap<>();

    @Override
    public List<PropostaCall> propostePerTeam(Team team) {
        List<PropostaCall> risultato = new ArrayList<>();
        if (team == null) {
            return risultato;
        }
        for (PropostaCall p : storage.values()) {
            if (p.getDestinatario().getNome().equals(team.getNome())) {
                risultato.add(p);
            }
        }
        return risultato;
    }

    @Override
    public void salva(PropostaCall proposta) {
        storage.put(UUID.randomUUID().toString(), proposta);
    }

    // --- Helper per i REST controller ---

    // id = UUID. Ritorna null se assente.
    public PropostaCall findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public List<PropostaCall> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id (UUID) di questa istanza, per identita' (==); null se non presente.
    public String idOf(PropostaCall proposta) {
        if (proposta == null) {
            return null;
        }
        for (Map.Entry<String, PropostaCall> e : storage.entrySet()) {
            if (e.getValue() == proposta) {
                return e.getKey();
            }
        }
        return null;
    }
}
