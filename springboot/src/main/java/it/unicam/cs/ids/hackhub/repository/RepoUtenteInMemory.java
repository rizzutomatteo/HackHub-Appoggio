package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Appartenenza;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Implementazione in-memory di RepoUtente.
// Chiave naturale: l'email dell'utente (Utente.getEmail()). L'id di dominio coincide con l'email.
@Repository
public class RepoUtenteInMemory implements RepoUtente {

    // chiave = email dell'utente
    private final Map<String, Utente> storage = new ConcurrentHashMap<>();

    // Autowire di RepoTeam per verificare l'appartenenza a un team.
    // Dipendenza a senso unico (RepoUtente -> RepoTeam) per evitare cicli.
    private final RepoTeam repoTeam;

    public RepoUtenteInMemory(RepoTeam repoTeam) {
        this.repoTeam = repoTeam;
    }

    @Override
    public boolean appartieneAdUnTeam(Utente u) {
        if (u == null) {
            return false;
        }
        for (Team team : ((RepoTeamInMemory) repoTeam).findAll()) {
            for (Appartenenza a : team.getMembri()) {
                if (a.getUtente().getEmail().equals(u.getEmail())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean esisteEmail(String email) {
        return storage.containsKey(email);
    }

    @Override
    public Utente trovaPerEmail(String email) {
        if (email == null) {
            return null;
        }
        return storage.get(email);
    }

    @Override
    public void salva(Utente u) {
        storage.put(u.getEmail(), u);
    }

    // --- Helper per i REST controller ---

    // id = chiave naturale (email); findById coincide con trovaPerEmail.
    public Utente findById(String id) {
        return trovaPerEmail(id);
    }

    public List<Utente> findAll() {
        return new ArrayList<>(storage.values());
    }

    // id di questa istanza = chiave naturale (email), o null.
    public String idOf(Utente u) {
        return u == null ? null : u.getEmail();
    }
}
