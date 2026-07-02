package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.service.ServiceSottomissione;

public class HandlerSottomissione {

    private final ServiceSottomissione service;
    private Iscrizione iscrizioneCorrente;
    private String titoloCorrente;
    private String contenutoCorrente;

    public HandlerSottomissione(ServiceSottomissione service) {
        this.service = service;
    }

    public void apriModulo(Iscrizione iscrizione) {
        if (service.sottomissioneConsentita(iscrizione)) {
            this.iscrizioneCorrente = iscrizione;
        }
    }

    public void caricaInvia(String titolo, String contenuto) {
        this.titoloCorrente = titolo;
        this.contenutoCorrente = contenuto;
    }

    public void conferma() {
        if (iscrizioneCorrente != null) {
            service.inviaSottomissione(iscrizioneCorrente, titoloCorrente, contenutoCorrente);
        }
        annulla();
    }

    public void annulla() {
        this.iscrizioneCorrente = null;
        this.titoloCorrente = null;
        this.contenutoCorrente = null;
    }
}
