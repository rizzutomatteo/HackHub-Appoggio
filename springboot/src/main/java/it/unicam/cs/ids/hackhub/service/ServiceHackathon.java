package it.unicam.cs.ids.hackhub.service;

import org.springframework.stereotype.Service;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Incarico;
import it.unicam.cs.ids.hackhub.entity.RuoloStaff;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoHackathon;
import it.unicam.cs.ids.hackhub.repository.RepoSottomissione;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
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

    // UC21: informazioni pubbliche di tutti gli hackathon (sola lettura, accesso libero del Visitatore).
    public List<Hackathon> hackathonPubblici() {
        return repoHackathon.tutti();
    }

    // UC22: l'Organizzatore assegnato aggiunge un Mentore a un hackathon non "Concluso".
    // La creazione dell'Incarico e le invarianti sono delegate all'Hackathon (Information Expert/Creator);
    // gli Incarico sono parte dell'aggregato Hackathon e si persistono via RepoHackathon.
    public boolean aggiungiMentore(Utente organizzatore, Hackathon hackathon, Utente utente) {
        if (organizzatore == null || hackathon == null || utente == null) {
            return false;
        }
        if (!organizzatoreAssegnato(organizzatore, hackathon)) {
            return false;   // solo l'Organizzatore assegnato puo' modificare lo staff (UC22 1a)
        }
        if (hackathon.aggiungiMentore(utente) == null) {
            return false;   // hackathon concluso (3b) o utente gia' Mentore (3a)
        }
        repoHackathon.salva(hackathon);
        return true;
    }

    // Scope via Incarico (D5): l'attore deve essere assegnato (ruolo = Organizzatore) all'hackathon.
    private boolean organizzatoreAssegnato(Utente organizzatore, Hackathon hackathon) {
        for (Incarico inc : hackathon.getStaff()) {
            if (inc.getRuolo() == RuoloStaff.Organizzatore && inc.getUtente().equals(organizzatore)) {
                return true;
            }
        }
        return false;
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
