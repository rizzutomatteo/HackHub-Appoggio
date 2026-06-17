package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.service.ServiceHackathon;

public class HandlerAvviaHackathon {

    private final ServiceHackathon service;

    public HandlerAvviaHackathon(ServiceHackathon service) {
        this.service = service;
    }

    public void avviaHackathon(Hackathon hackathon) {
        service.avvia(hackathon);
    }
}
