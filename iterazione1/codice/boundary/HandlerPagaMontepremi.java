package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.service.ServicePagamento;

// Boundary di UC16: l'Organizzatore eroga il montepremi al team vincitore.
public class HandlerPagaMontepremi {

    private final ServicePagamento service;

    public HandlerPagaMontepremi(ServicePagamento service) {
        this.service = service;
    }

    public void pagaMontepremi(Hackathon hackathon) {
        service.paga(hackathon);
    }
}
