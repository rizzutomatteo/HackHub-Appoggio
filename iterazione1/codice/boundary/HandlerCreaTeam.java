package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.service.ServiceTeam;

public class HandlerCreaTeam {

    private final ServiceTeam service;

    public HandlerCreaTeam(ServiceTeam service) {
        this.service = service;
    }

    public void nuovoTeam() {
    }

    public void inserisceNome(String nome) {
        service.creaTeam(nome);
    }
}
