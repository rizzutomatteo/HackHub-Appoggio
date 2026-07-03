package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.entity.Team;

import java.util.List;

public interface RepoIscrizione {

    boolean esisteIscrizione(Team team, Hackathon hackathon);

    void salva(Iscrizione i);

    List<Iscrizione> iscrizioniDi(Team team);

    // UC11 (iter.3): il team e' iscritto all'hackathon e l'hackathon e' nello stato InCorso
    // (lettura del valore dello stato, NON una transizione).
    boolean iscrizioneInCorso(Team team, Hackathon hackathon);
}
