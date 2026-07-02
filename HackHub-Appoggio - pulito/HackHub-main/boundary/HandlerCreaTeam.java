package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceTeam;

public class HandlerCreaTeam {

    private final ServiceTeam service;

    public HandlerCreaTeam(ServiceTeam service) {
        this.service = service;
    }

    // utente = utente autenticato (stub: autenticazione fuori scope in iter.1)
    public void nuovoTeam(Utente utente) {
        service.iniziaCreazioneTeam(utente);
    }

    public void inserisceNome(String nome) {
        service.creaTeam(nome);
    }
}
