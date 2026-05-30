package it.unicam.cs.ids.hackhub.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Iscrizione {

    private final Team team;
    private final Hackathon hackathon;
    private final LocalDate dataIscrizione;
    private Sottomissione sottomissione;

    public Iscrizione(Team team, Hackathon hackathon, LocalDate dataIscrizione) {
        this.team = team;
        this.hackathon = hackathon;
        this.dataIscrizione = dataIscrizione;
    }

    public Sottomissione creaSottomissione(String titolo, String contenuto) {
        this.sottomissione = new Sottomissione(this, titolo, contenuto, LocalDateTime.now());
        return this.sottomissione;
    }

    public Team getTeam() {
        return team;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public LocalDate getDataIscrizione() {
        return dataIscrizione;
    }

    public Sottomissione getSottomissione() {
        return sottomissione;
    }
}
