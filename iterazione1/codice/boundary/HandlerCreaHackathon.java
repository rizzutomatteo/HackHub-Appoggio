package it.unicam.cs.ids.hackhub.boundary;

import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.service.ServiceHackathon;

import java.time.LocalDate;
import java.util.List;

public class HandlerCreaHackathon {

    private final ServiceHackathon service;

    public HandlerCreaHackathon(ServiceHackathon service) {
        this.service = service;
    }

    public void nuovoHackathon() {
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
                              giudice,
                              mentori);
    }
}
