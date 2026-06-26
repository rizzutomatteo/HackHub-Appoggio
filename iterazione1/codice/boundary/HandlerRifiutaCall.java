package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.PropostaCall;
import it.unicam.cs.ids.hackhub.service.ServiceCall;

// Boundary di UC15: l'Amministratore del Team destinatario rifiuta la proposta di call.
public class HandlerRifiutaCall {

    private final ServiceCall service;

    public HandlerRifiutaCall(ServiceCall service) {
        this.service = service;
    }

    public void rifiutaProposta(PropostaCall proposta) {
        service.rifiutaProposta(proposta);
    }
}
