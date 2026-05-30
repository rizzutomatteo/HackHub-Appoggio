package it.unicam.cs.ids.hackhub.entity;

public class Valutazione {

    private final Sottomissione sottomissione;
    private final String giudizio;
    private final int punteggio;
    private final Utente giudice;

    public Valutazione(Sottomissione sottomissione, String giudizio, int punteggio, Utente giudice) {
        this.sottomissione = sottomissione;
        this.giudizio = giudizio;
        this.punteggio = punteggio;
        this.giudice = giudice;
    }

    public Sottomissione getSottomissione() {
        return sottomissione;
    }

    public String getGiudizio() {
        return giudizio;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public Utente getGiudice() {
        return giudice;
    }
}
