package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.service.ServiceHackathon;

public class HandlerIniziaFaseValutazione {

    private final ServiceHackathon service;

    public HandlerIniziaFaseValutazione(ServiceHackathon service) {
        this.service = service;
    }

    public void iniziaFaseValutazione(Hackathon hackathon) {
        service.iniziaFaseValutazione(hackathon);
    }
}
