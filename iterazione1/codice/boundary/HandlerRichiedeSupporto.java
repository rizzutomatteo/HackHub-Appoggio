package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.service.ServiceSupporto;

// Boundary di UC11: apre il modulo (team, hackathon) e poi conferma con il messaggio.
public class HandlerRichiedeSupporto {

    private final ServiceSupporto service;
    private Team teamCorrente;
    private Hackathon hackathonCorrente;

    public HandlerRichiedeSupporto(ServiceSupporto service) {
        this.service = service;
    }

    public void richiediSupporto(Team team, Hackathon hackathon) {
        this.teamCorrente = team;
        this.hackathonCorrente = hackathon;
    }

    public void confermaRichiesta(String messaggio) {
        if (teamCorrente != null && hackathonCorrente != null) {
            service.richiediSupporto(teamCorrente, hackathonCorrente, messaggio);
        }
        this.teamCorrente = null;
        this.hackathonCorrente = null;
    }
}
