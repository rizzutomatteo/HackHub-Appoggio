package it.unicam.cs.ids.hackhub.entity;

public class Appartenenza {

    private final Utente utente;
    private final Team team;
    private final boolean amministratore;

    public Appartenenza(Utente utente, Team team, boolean amministratore) {
        this.utente = utente;
        this.team = team;
        this.amministratore = amministratore;
    }

    public Utente getUtente() {
        return utente;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isAmministratore() {
        return amministratore;
    }
}
