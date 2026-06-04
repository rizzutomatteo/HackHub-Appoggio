package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceHackathon;

import java.time.LocalDate;
import java.util.List;

public class HandlerCreaHackathon {

    private final ServiceHackathon service;
    private Utente organizzatoreCorrente;

    public HandlerCreaHackathon(ServiceHackathon service) {
        this.service = service;
    }

    // organizzatore = utente autenticato (stub: autenticazione fuori scope in iter.1)
    public void nuovoHackathon(Utente organizzatore) {
        this.organizzatoreCorrente = organizzatore;
    }

    public void inserisceDati(String nome,
                              LocalDate dataInizio,
                              LocalDate dataFine,
                              String luogo,
                              String regolamento,
                              LocalDate scadenzaIscrizioni,
                              int dimensioneMaxTeam,
                              long montepremi,
                              Utente giudice,
                              List<Utente> mentori) {
        service.creaHackathon(nome,
                              dataInizio,
                              dataFine,
                              luogo,
                              regolamento,
                              scadenzaIscrizioni,
                              dimensioneMaxTeam,
                              montepremi,
                              organizzatoreCorrente,
                              giudice,
                              mentori);
    }
}
