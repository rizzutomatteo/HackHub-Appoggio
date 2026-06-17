package it.unicam.cs.ids.hackhub.entity;

import java.time.LocalDate;

public class Invito {

    private final Team team;
    private final Utente utenteInvitato;
    private final LocalDate dataInvito;
    private StatoInvito stato;

    public Invito(Team team, Utente utenteInvitato) {
        this.team = team;
        this.utenteInvitato = utenteInvitato;
        this.dataInvito = LocalDate.now();
        this.stato = StatoInvito.Pendente;
    }

    public void accetta() {
        this.stato = StatoInvito.Accettato;
    }

    public void rifiuta() {
        this.stato = StatoInvito.Rifiutato;
    }

    public Team getTeam() {
        return team;
    }

    public Utente getUtenteInvitato() {
        return utenteInvitato;
    }

    public LocalDate getDataInvito() {
        return dataInvito;
    }

    public StatoInvito getStato() {
        return stato;
    }
}
