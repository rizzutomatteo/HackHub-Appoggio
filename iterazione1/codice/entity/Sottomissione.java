package it.unicam.cs.ids.hackhub.entity;

import java.time.LocalDateTime;

public class Sottomissione {

    private final Iscrizione iscrizione;
    private final String titolo;
    private final String contenuto;
    private final LocalDateTime dataOra;
    private Valutazione valutazione;

    public Sottomissione(Iscrizione iscrizione, String titolo, String contenuto, LocalDateTime dataOra) {
        this.iscrizione = iscrizione;
        this.titolo = titolo;
        this.contenuto = contenuto;
        this.dataOra = dataOra;
    }

    public Valutazione registraValutazione(String giudizio, int punteggio, Utente giudice) {
        if (giudizio == null || giudizio.isEmpty()) {
            return null;
        }
        if (punteggio < 0 || punteggio > 10) {
            return null;
        }
        this.valutazione = new Valutazione(this, giudizio, punteggio, giudice);
        return this.valutazione;
    }

    public Iscrizione getIscrizione() {
        return iscrizione;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getContenuto() {
        return contenuto;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public Valutazione getValutazione() {
        return valutazione;
    }
}
