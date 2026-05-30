package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Utente;

public interface RepoUtente {

    boolean appartieneAdUnTeam(Utente u);
}
