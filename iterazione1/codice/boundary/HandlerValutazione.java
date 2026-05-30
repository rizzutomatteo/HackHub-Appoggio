package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import it.unicam.cs.ids.hackhub.service.ServiceValutazione;

public class HandlerValutazione {

    private final ServiceValutazione service;
    private Sottomissione sottomissioneSelezionata;

    public HandlerValutazione(ServiceValutazione service) {
        this.service = service;
    }

    public void apriElenco(Hackathon hackathon) {
        service.elencoSottomissioni(hackathon);
    }

    public void selezionaSottomissione(Sottomissione sub) {
        this.sottomissioneSelezionata = service.dettagli(sub);
    }

    public void valuta(String giudizio, int punteggio) {
        if (sottomissioneSelezionata != null) {
            service.valuta(sottomissioneSelezionata, giudizio, punteggio);
        }
    }
}
