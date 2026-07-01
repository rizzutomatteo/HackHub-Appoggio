package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Segnalazione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;

import java.util.List;

// Pure Fabrication: persistenza delle Segnalazione. Solo i control vi accedono.
public interface RepoSegnalazione {

    // UC17 (3a): esiste gia' una segnalazione "Aperta" dello stesso Mentore per (team, hackathon)?
    boolean esisteSegnalazioneAperta(Utente mentore, Team team, Hackathon hackathon);

    // UC18: scope via Incarico — le segnalazioni degli hackathon a cui l'Organizzatore e' assegnato.
    List<Segnalazione> trovaPerHackathonDellOrganizzatore(Utente organizzatore);

    void salva(Segnalazione seg);
}
