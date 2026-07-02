package it.unicam.cs.ids.hackhub.entity;

import it.unicam.cs.ids.hackhub.observer.Notifica;

import java.time.LocalDate;

// UC11: richiesta di supporto aperta da un Membro di un Team iscritto a un Hackathon "in corso".
// Realizza Notifica (payload dell'Observer): i Mentori dell'hackathon sono avvisati.
// stato e' un enum (Aperta/Gestita/Chiusa), NON un pattern State.
public class RichiestaSupporto implements Notifica {

    private final Team team;
    private final Hackathon hackathon;
    private final String messaggio;
    private final LocalDate dataRichiesta;
    private StatoRichiesta stato;

    public RichiestaSupporto(Team team, Hackathon hackathon, String messaggio) {
        this.team = team;
        this.hackathon = hackathon;
        this.messaggio = messaggio;
        this.dataRichiesta = LocalDate.now();
        this.stato = StatoRichiesta.Aperta;
    }

    public Team getTeam() {
        return team;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public LocalDate getDataRichiesta() {
        return dataRichiesta;
    }

    public StatoRichiesta getStato() {
        return stato;
    }
}
