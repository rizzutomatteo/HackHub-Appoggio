package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Invito;
import it.unicam.cs.ids.hackhub.service.ServiceInvito;

public class HandlerAccettaInvito {

    private final ServiceInvito service;

    public HandlerAccettaInvito(ServiceInvito service) {
        this.service = service;
    }

    public void accettaInvito(Invito invito) {
        service.accetta(invito);
    }
}
