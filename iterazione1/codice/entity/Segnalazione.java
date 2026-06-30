package it.unicam.cs.ids.hackhub.entity;

import it.unicam.cs.ids.hackhub.observer.Notifica;

import java.time.LocalDate;

// UC17: segnalazione di una violazione del regolamento da parte di un Team, aperta da un Mentore.
// Realizza Notifica (payload dell'Observer): l'Organizzatore dell'hackathon e' avvisato (UC17) e
// decide (UC18). stato e' un enum (Aperta/Gestita/Archiviata), NON un pattern State.
// Information Expert: la Segnalazione conosce e fa evolvere il proprio stato (gestisci/archivia).
public class Segnalazione implements Notifica {

    private final Utente segnalante;        // Mentore (via Incarico)
    private final Team team;                // team segnalato
    private final Hackathon hackathon;      // contesto
    private final String descrizione;
    private final LocalDate dataSegnalazione;
    private StatoSegnalazione stato;
    private String decisione;               // esito della decisione dell'Organizzatore (UC18)

    public Segnalazione(Utente segnalante, Team team, Hackathon hackathon, String descrizione) {
        this.segnalante = segnalante;
        this.team = team;
        this.hackathon = hackathon;
        this.descrizione = descrizione;
        this.dataSegnalazione = LocalDate.now();
        this.stato = StatoSegnalazione.Aperta;
    }

    // UC18: l'Organizzatore prende in carico la segnalazione registrando la decisione del caso.
    public void gestisci(String decisione) {
        this.decisione = decisione;
        this.stato = StatoSegnalazione.Gestita;
    }

    // UC18 3b: l'Organizzatore archivia la segnalazione senza provvedimenti.
    public void archivia() {
        this.stato = StatoSegnalazione.Archiviata;
    }

    public Utente getSegnalante() {
        return segnalante;
    }

    public Team getTeam() {
        return team;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDate getDataSegnalazione() {
        return dataSegnalazione;
    }

    public StatoSegnalazione getStato() {
        return stato;
    }

    public String getDecisione() {
        return decisione;
    }
}
