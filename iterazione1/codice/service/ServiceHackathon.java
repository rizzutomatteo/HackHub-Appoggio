package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoHackathon;
import it.unicam.cs.ids.hackhub.repository.RepoSottomissione;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ServiceHackathon {

    private final RepoHackathon repoHackathon;
    private final RepoSottomissione repoSottomissione;

    public ServiceHackathon(RepoHackathon repoHackathon, RepoSottomissione repoSottomissione) {
        this.repoHackathon = repoHackathon;
        this.repoSottomissione = repoSottomissione;
    }

    public boolean creaHackathon(String nome,
                                 LocalDate dataInizio,
                                 LocalDate dataFine,
                                 String luogo,
                                 String regolamento,
                                 LocalDate scadenzaIscrizioni,
                                 int dimensioneMaxTeam,
                                 long montepremi,
                                 Utente organizzatore,
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
        if (organizzatore == null) {
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
        h.assegnaStaff(organizzatore, giudice, mentori);
        repoHackathon.salva(h);
        return true;
    }

    public boolean avvia(Hackathon hackathon) {
        if (hackathon == null) {
            return false;
        }
        if (!hackathon.avvia()) {
            return false;
        }
        repoHackathon.salva(hackathon);
        return true;
    }

    public boolean iniziaFaseValutazione(Hackathon hackathon) {
        if (hackathon == null) {
            return false;
        }
        if (!hackathon.iniziaFaseValutazione()) {
            return false;
        }
        repoHackathon.salva(hackathon);
        return true;
    }

    public List<Sottomissione> preparaProclamazione(Hackathon hackathon) {
        if (hackathon == null || !hackathon.valutazioneConsentita()) {
            return null;
        }
        List<Sottomissione> sottomissioni = repoSottomissione.sottomissioniDi(hackathon);
        if (sottomissioni.isEmpty()) {
            return null;
        }
        if (!tutteValutate(sottomissioni)) {
            return null;
        }
        return classificaPerPunteggio(sottomissioni);
    }

    public boolean proclamaVincitore(Hackathon hackathon, Team teamVincitore) {
        if (hackathon == null || teamVincitore == null) {
            return false;
        }
        if (!hackathon.proclamaVincitore(teamVincitore)) {
            return false;
        }
        repoHackathon.salva(hackathon);
        return true;
    }

    private boolean tutteValutate(List<Sottomissione> sottomissioni) {
        for (Sottomissione sub : sottomissioni) {
            if (sub.getValutazione() == null) {
                return false;
            }
        }
        return true;
    }

    private List<Sottomissione> classificaPerPunteggio(List<Sottomissione> sottomissioni) {
        List<Sottomissione> classifica = new ArrayList<>(sottomissioni);
        classifica.sort(Comparator.comparingInt((Sottomissione sub) -> sub.getValutazione().getPunteggio()).reversed());
        return classifica;
    }
}
