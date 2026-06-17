package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.service.ServiceHackathon;

import java.util.List;

public class HandlerProclamaVincitore {

    private final ServiceHackathon service;
    private List<Sottomissione> classifica;

    public HandlerProclamaVincitore(ServiceHackathon service) {
        this.service = service;
    }

    public void apriProclamazione(Hackathon hackathon) {
        this.classifica = service.preparaProclamazione(hackathon);
    }

    public void proclama(Hackathon hackathon, Team teamVincitore) {
        if (classifica == null) {
            return;
        }
        service.proclamaVincitore(hackathon, teamVincitore);
        this.classifica = null;
    }
}
