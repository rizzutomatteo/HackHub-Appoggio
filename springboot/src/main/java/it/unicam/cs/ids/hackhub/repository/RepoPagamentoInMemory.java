package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Pagamento;
import it.unicam.cs.ids.hackhub.entity.StatoPagamento;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoPagamento.
// Nessuna chiave naturale: id = UUID generato in salva(). Confronti per chiave naturale
// (Hackathon.getNome()), mai con equals()/==.
@Repository
public class RepoPagamentoInMemory implements RepoPagamento {

    // chiave = UUID generato in salva()
    private final Map<String, Pagamento> storage = new ConcurrentHashMap<>();

    @Override
    public boolean esistePagamentoCompletato(Hackathon hackathon) {
        if (hackathon == null) {
            return false;
        }
        for (Pagamento pag : storage.values()) {
            if (pag.getStato() == StatoPagamento.Completato
                    && pag.getHackathon().getNome().equals(hackathon.getNome())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void salva(Pagamento pag) {
        storage.put(UUID.randomUUID().toString(), pag);
    }

    // --- Helper per i REST controller ---

    // id = UUID. Ritorna null se assente.
    public Pagamento findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public List<Pagamento> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id (UUID) di questa istanza, per identita' (==); null se non presente.
    public String idOf(Pagamento pag) {
        if (pag == null) {
            return null;
        }
        for (Map.Entry<String, Pagamento> e : storage.entrySet()) {
            if (e.getValue() == pag) {
                return e.getKey();
            }
        }
        return null;
    }
}
