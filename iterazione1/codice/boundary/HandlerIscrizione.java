package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.service.ServiceIscrizione;

public class HandlerIscrizione {

    private final ServiceIscrizione service;

    public HandlerIscrizione(ServiceIscrizione service) {
        this.service = service;
    }

    public void richiediIscrizione(Team team, Hackathon hackathon) {
        service.iscriviTeam(team, hackathon);
    }
}
