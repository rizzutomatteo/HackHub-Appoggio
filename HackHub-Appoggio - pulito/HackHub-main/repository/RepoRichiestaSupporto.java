package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.RichiestaSupporto;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;

import java.util.List;

// Pure Fabrication: persistenza delle RichiestaSupporto. Solo i control vi accedono.
public interface RepoRichiestaSupporto {

    boolean esisteRichiestaAperta(Team team, Hackathon hackathon);

    // Scope via Incarico: le richieste degli hackathon a cui il Mentore e' assegnato (ruolo = Mentore).
    List<RichiestaSupporto> trovaPerHackathonDelMentore(Utente mentore);

    void salva(RichiestaSupporto r);
}
