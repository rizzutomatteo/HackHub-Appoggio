package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Segnalazione;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceSegnalazione;

// Boundary di UC18: l'Organizzatore consulta le proprie segnalazioni e le gestisce o archivia.
public class HandlerGestisciSegnalazione {

    private final ServiceSegnalazione service;

    public HandlerGestisciSegnalazione(ServiceSegnalazione service) {
        this.service = service;
    }

    public void visualizzaSegnalazioni(Utente organizzatore) {
        service.segnalazioniDellOrganizzatore(organizzatore);
    }

    public void gestisci(Segnalazione segnalazione, String decisione) {
        service.gestisci(segnalazione, decisione);
    }

    public void archivia(Segnalazione segnalazione) {
        service.archivia(segnalazione);
    }
}
