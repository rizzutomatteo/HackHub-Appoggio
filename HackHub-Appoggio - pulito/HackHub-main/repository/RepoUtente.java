package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Utente;

public interface RepoUtente {

    boolean appartieneAdUnTeam(Utente u);

    // UC19: l'email e' univoca (identita' dell'utente).
    boolean esisteEmail(String email);

    // UC20: recupera l'utente dalle credenziali (null se inesistente).
    Utente trovaPerEmail(String email);

    // UC19: persiste il nuovo Utente registrato.
    void salva(Utente u);
}
