package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.repository.RepoIscrizione;

public class ServiceIscrizione {

    private final RepoIscrizione repoIscrizione;

    public ServiceIscrizione(RepoIscrizione repoIscrizione) {
        this.repoIscrizione = repoIscrizione;
    }

    public boolean iscriviTeam(Team team, Hackathon hackathon) {
        if (team == null || hackathon == null) {
            return false;
        }
        if (repoIscrizione.esisteIscrizione(team, hackathon)) {
            return false;
        }
        if (!hackathon.iscrivibile(team)) {
            return false;
        }

        Iscrizione i = hackathon.creaIscrizione(team);
        repoIscrizione.salva(i);
        return true;
    }
}
