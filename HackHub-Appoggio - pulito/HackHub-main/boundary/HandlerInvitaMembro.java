package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceInvito;

public class HandlerInvitaMembro {

    private final ServiceInvito service;

    public HandlerInvitaMembro(ServiceInvito service) {
        this.service = service;
    }

    public void invitaMembro(Team team, Utente utenteInvitato) {
        service.invita(team, utenteInvitato);
    }
}
