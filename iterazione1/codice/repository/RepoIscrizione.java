package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.entity.Team;

public interface RepoIscrizione {

    boolean esisteIscrizione(Team team, Hackathon hackathon);

    void salva(Iscrizione i);
}
