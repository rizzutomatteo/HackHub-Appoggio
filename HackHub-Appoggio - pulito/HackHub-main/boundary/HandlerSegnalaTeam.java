package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceSegnalazione;

// Boundary di UC17: apre il modulo (team, hackathon) e poi conferma con la descrizione della violazione.
public class HandlerSegnalaTeam {

    private final ServiceSegnalazione service;
    private Utente mentoreCorrente;
    private Team teamCorrente;
    private Hackathon hackathonCorrente;

    public HandlerSegnalaTeam(ServiceSegnalazione service) {
        this.service = service;
    }

    // mentore = utente autenticato (stub: autenticazione fuori scope)
    public void segnalaTeam(Utente mentore, Team team, Hackathon hackathon) {
        this.mentoreCorrente = mentore;
        this.teamCorrente = team;
        this.hackathonCorrente = hackathon;
    }

    public void confermaSegnalazione(String descrizione) {
        if (mentoreCorrente != null && teamCorrente != null && hackathonCorrente != null) {
            service.segnala(mentoreCorrente, teamCorrente, hackathonCorrente, descrizione);
        }
        this.mentoreCorrente = null;
        this.teamCorrente = null;
        this.hackathonCorrente = null;
    }
}
