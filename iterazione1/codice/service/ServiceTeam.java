package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoTeam;
import it.unicam.cs.ids.hackhub.repository.RepoUtente;

public class ServiceTeam {

    private final RepoUtente repoUtente;
    private final RepoTeam repoTeam;
    private Utente creatoreCorrente;

    public ServiceTeam(RepoUtente repoUtente, RepoTeam repoTeam) {
        this.repoUtente = repoUtente;
        this.repoTeam = repoTeam;
    }

    public boolean iniziaCreazioneTeam(Utente utente) {
        if (utente == null) {
            return false;
        }
        if (repoUtente.appartieneAdUnTeam(utente)) {
            return false;
        }
        this.creatoreCorrente = utente;
        return true;
    }

    public boolean creaTeam(String nome) {
        if (creatoreCorrente == null) {
            return false;
        }
        if (nome == null || nome.isEmpty()) {
            return false;
        }
        if (repoTeam.esisteNome(nome)) {
            return false;
        }

        Team t = new Team(nome, creatoreCorrente);
        repoTeam.salva(t);
        this.creatoreCorrente = null;
        return true;
    }
}
