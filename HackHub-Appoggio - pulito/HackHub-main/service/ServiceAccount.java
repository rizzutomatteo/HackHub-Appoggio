package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoUtente;

// Control (GRASP Controller) dell'accesso (UC19 Registrazione, UC20 Autenticazione). Creator dell'Utente.
// Autenticazione "sottile" (D4): credenziali minime; niente hashing robusto/sessioni/token (fuori scope).
public class ServiceAccount {

    private final RepoUtente repoUtente;

    public ServiceAccount(RepoUtente repoUtente) {
        this.repoUtente = repoUtente;
    }

    // UC19: crea un nuovo Utente se i dati sono validi e l'email non e' gia' in uso.
    public boolean registra(String nome, String email, String password) {
        if (!datiValidi(nome, email, password)) {
            return false;   // dati non validi (UC19 4b)
        }
        if (repoUtente.esisteEmail(email)) {
            return false;   // email gia' in uso (UC19 4a)
        }

        Utente u = new Utente(nome, email, password);
        repoUtente.salva(u);
        return true;
    }

    // UC20: verifica le credenziali e restituisce l'utente autenticato (null se non valide).
    public Utente autentica(String email, String password) {
        Utente utente = repoUtente.trovaPerEmail(email);
        if (!verificaCredenziali(utente, password)) {
            return null;   // credenziali non valide (UC20 4a)
        }
        return utente;
    }

    // Validazione minimale (D4): campi presenti ed email formalmente plausibile.
    private boolean datiValidi(String nome, String email, String password) {
        return nome != null && !nome.isEmpty()
                && email != null && email.contains("@")
                && password != null && !password.isEmpty();
    }

    // Verifica "sottile" (UC20 4a): esistenza dell'utente + password, senza distinguere il campo errato.
    private boolean verificaCredenziali(Utente utente, String password) {
        return utente != null && utente.verificaPassword(password);
    }
}
