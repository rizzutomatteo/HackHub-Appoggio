package it.unicam.cs.ids.hackhub.entity;

public class Incarico {

    private final Utente utente;
    private final Hackathon hackathon;
    private final RuoloStaff ruolo;

    public Incarico(Utente utente, Hackathon hackathon, RuoloStaff ruolo) {
        this.utente = utente;
        this.hackathon = hackathon;
        this.ruolo = ruolo;
    }

    public Utente getUtente() {
        return utente;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public RuoloStaff getRuolo() {
        return ruolo;
    }
}
