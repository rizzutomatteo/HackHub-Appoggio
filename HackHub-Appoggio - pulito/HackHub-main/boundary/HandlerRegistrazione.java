package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.service.ServiceAccount;

// Boundary di UC19: apre il modulo di registrazione e poi inoltra i dati inseriti.
public class HandlerRegistrazione {

    private final ServiceAccount service;

    public HandlerRegistrazione(ServiceAccount service) {
        this.service = service;
    }

    // apre il modulo (nome, email, password): nessuna logica di dominio nel boundary
    public void registrati() {
    }

    public void inserisceDati(String nome, String email, String password) {
        service.registra(nome, email, password);
    }
}
