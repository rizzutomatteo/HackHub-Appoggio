package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.service.ServiceHackathon;

// Boundary di UC21: il Visitatore consulta le informazioni pubbliche sugli hackathon (sola lettura,
// nessuna autenticazione). Riusa il ServiceHackathon esistente.
public class HandlerAccediInformazioniPubbliche {

    private final ServiceHackathon service;

    public HandlerAccediInformazioniPubbliche(ServiceHackathon service) {
        this.service = service;
    }

    public void consultaHackathon() {
        service.hackathonPubblici();
    }
}
