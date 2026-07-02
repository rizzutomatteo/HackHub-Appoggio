package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.RichiestaSupporto;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceCall;

import java.time.LocalDateTime;

// Boundary di UC13: seleziona la richiesta, poi conferma lo slot proposto.
public class HandlerProponeCall {

    private final ServiceCall service;
    private Utente mentoreCorrente;
    private RichiestaSupporto richiestaCorrente;

    public HandlerProponeCall(ServiceCall service) {
        this.service = service;
    }

    // mentore = utente autenticato (stub: autenticazione fuori scope)
    public void proponiCall(Utente mentore, RichiestaSupporto richiesta) {
        this.mentoreCorrente = mentore;
        this.richiestaCorrente = richiesta;
    }

    public void confermaSlot(LocalDateTime slot) {
        if (mentoreCorrente != null && richiestaCorrente != null) {
            service.proponiCall(mentoreCorrente, richiestaCorrente, slot);
        }
        this.mentoreCorrente = null;
        this.richiestaCorrente = null;
    }
}
