package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import it.unicam.cs.ids.hackhub.repository.RepoSottomissione;

public class ServiceSottomissione {

    private final RepoSottomissione repoSottomissione;

    public ServiceSottomissione(RepoSottomissione repoSottomissione) {
        this.repoSottomissione = repoSottomissione;
    }

    public boolean sottomissioneConsentita(Iscrizione iscrizione) {
        if (iscrizione == null) {
            return false;
        }
        return iscrizione.getHackathon().sottomissioneConsentita();
    }

    public boolean inviaSottomissione(Iscrizione iscrizione, String titolo, String contenuto) {
        if (iscrizione == null) {
            return false;
        }
        if (!iscrizione.getHackathon().sottomissioneConsentita()) {
            return false;
        }
        if (titolo == null || titolo.isEmpty()) {
            return false;
        }
        if (contenuto == null) {
            return false;
        }

        Sottomissione sub = iscrizione.creaSottomissione(titolo, contenuto);
        repoSottomissione.salva(sub);
        return true;
    }
}
