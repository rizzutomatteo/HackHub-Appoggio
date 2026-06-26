package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceSupporto;

// Boundary di UC12: mostra al Mentore le richieste dei propri hackathon (sola lettura).
public class HandlerVisualizzaRichieste {

    private final ServiceSupporto service;

    public HandlerVisualizzaRichieste(ServiceSupporto service) {
        this.service = service;
    }

    // mentore = utente autenticato (stub: autenticazione fuori scope)
    public void visualizzaRichieste(Utente mentore) {
        service.richiesteDelMentore(mentore);
    }
}
