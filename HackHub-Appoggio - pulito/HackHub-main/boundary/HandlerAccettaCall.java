package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.PropostaCall;
import it.unicam.cs.ids.hackhub.service.ServiceCall;

// Boundary di UC14: l'Amministratore del Team destinatario accetta la proposta di call.
public class HandlerAccettaCall {

    private final ServiceCall service;

    public HandlerAccettaCall(ServiceCall service) {
        this.service = service;
    }

    public void accettaProposta(PropostaCall proposta) {
        service.accettaProposta(proposta);
    }
}
