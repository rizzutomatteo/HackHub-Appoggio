package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Invito;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;

public interface RepoInvito {

    boolean esisteInvitoPendente(Team team, Utente u);

    void salva(Invito inv);
}
