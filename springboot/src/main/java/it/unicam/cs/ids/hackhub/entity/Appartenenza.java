package it.unicam.cs.ids.hackhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    public Team getTeam() {
        return team;
    }

    public boolean isAmministratore() {
        return amministratore;
    }
}
