package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.service.ServiceAccount;

// Boundary di UC20: apre il modulo di accesso e poi inoltra le credenziali inserite.
public class HandlerAutenticazione {

    private final ServiceAccount service;

    public HandlerAutenticazione(ServiceAccount service) {
        this.service = service;
    }

    // apre il modulo (email, password)
    public void accedi() {
    }

    public void inserisceCredenziali(String email, String password) {
        service.autentica(email, password);
    }
}
