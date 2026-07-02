package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceHackathon;

// Boundary di UC22: l'Organizzatore aggiunge un Mentore a un hackathon gia' creato. Riusa ServiceHackathon.
public class HandlerAggiungiMentore {

    private final ServiceHackathon service;

    public HandlerAggiungiMentore(ServiceHackathon service) {
        this.service = service;
    }

    // organizzatore = utente autenticato (stub: autenticazione fuori scope)
    public void aggiungiMentore(Utente organizzatore, Hackathon hackathon, Utente utente) {
        service.aggiungiMentore(organizzatore, hackathon, utente);
    }
}
