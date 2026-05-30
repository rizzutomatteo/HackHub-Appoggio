package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoHackathon;

import java.time.LocalDate;
import java.util.List;

public class ServiceHackathon {

    private final RepoHackathon repoHackathon;

    public ServiceHackathon(RepoHackathon repoHackathon) {
        this.repoHackathon = repoHackathon;
    }

    public boolean creaHackathon(String nome,
                                 LocalDate dataInizio,
                                 LocalDate dataFine,
                                 String luogo,
                                 String regolamento,
                                 LocalDate scadenzaIscrizioni,
                                 int dimensioneMaxTeam,
                                 long montepremi,
                                 Utente giudice,
                                 List<Utente> mentori) {
        if (nome == null || nome.isEmpty()) {
            return false;
        }
        if (dataInizio == null || dataFine == null || scadenzaIscrizioni == null) {
            return false;
        }
        if (luogo == null || regolamento == null) {
            return false;
        }
        if (dimensioneMaxTeam <= 0) {
            return false;
        }
        if (giudice == null) {
            return false;
        }
        if (mentori == null || mentori.isEmpty()) {
            return false;
        }

        Hackathon h = new Hackathon(nome,
                                    dataInizio,
                                    dataFine,
                                    luogo,
                                    regolamento,
                                    scadenzaIscrizioni,
                                    dimensioneMaxTeam,
                                    montepremi);
        h.assegnaStaff(null, giudice, mentori);
        repoHackathon.salva(h);
        return true;
    }
}
