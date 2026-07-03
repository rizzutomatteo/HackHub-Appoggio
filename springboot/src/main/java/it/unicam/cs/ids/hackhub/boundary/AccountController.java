package it.unicam.cs.ids.hackhub.boundary;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoUtenteInMemory;
import it.unicam.cs.ids.hackhub.service.ServiceAccount;

// Boundary REST per l'accesso (UC19 Registrazione, UC20 Autenticazione).
// Delega la logica a ServiceAccount; usa RepoUtenteInMemory solo per gli helper.
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final ServiceAccount serviceAccount;
    private final RepoUtenteInMemory repoUtente;

    public AccountController(ServiceAccount serviceAccount, RepoUtenteInMemory repoUtente) {
        this.serviceAccount = serviceAccount;
        this.repoUtente = repoUtente;
    }

    // UC19: registra un nuovo utente. L'id di dominio dell'Utente coincide con l'email.
    @PostMapping("/registrati")
    public ResponseEntity<?> registrati(@RequestBody RegistrazioneRequest richiesta) {
        if (richiesta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errore", "corpo della richiesta mancante"));
        }
        boolean creato = serviceAccount.registra(richiesta.nome(), richiesta.email(), richiesta.password());
        if (!creato) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errore", "registrazione non riuscita"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", richiesta.email()));
    }

    // UC20: autentica un utente esistente. Utente != null -> 200; null -> 401.
    @PostMapping("/accedi")
    public ResponseEntity<?> accedi(@RequestBody AccessoRequest richiesta) {
        if (richiesta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errore", "corpo della richiesta mancante"));
        }
        Utente utente = serviceAccount.autentica(richiesta.email(), richiesta.password());
        if (utente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("errore", "credenziali non valide"));
        }
        return ResponseEntity.ok(Map.of("email", utente.getEmail()));
    }

    public record RegistrazioneRequest(String nome, String email, String password) {
    }

    public record AccessoRequest(String email, String password) {
    }
}
