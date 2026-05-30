package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Incarico;
import it.unicam.cs.ids.hackhub.entity.RuoloStaff;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.entity.Valutazione;
import it.unicam.cs.ids.hackhub.repository.RepoSottomissione;
import it.unicam.cs.ids.hackhub.repository.RepoValutazione;

import java.util.List;

public class ServiceValutazione {

    private final RepoSottomissione repoSottomissione;
    private final RepoValutazione repoValutazione;

    public ServiceValutazione(RepoSottomissione repoSottomissione, RepoValutazione repoValutazione) {
        this.repoSottomissione = repoSottomissione;
        this.repoValutazione = repoValutazione;
    }

    public List<Sottomissione> elencoSottomissioni(Hackathon hackathon) {
        return repoSottomissione.sottomissioniDi(hackathon);
    }

    public Sottomissione dettagli(Sottomissione sub) {
        if (sub == null) {
            return null;
        }
        if (!sub.getIscrizione().getHackathon().valutazioneConsentita()) {
            return null;
        }
        return sub;
    }

    public boolean valuta(Sottomissione sub, String giudizio, int punteggio) {
        if (sub == null) {
            return false;
        }
        Hackathon h = sub.getIscrizione().getHackathon();
        if (!h.valutazioneConsentita()) {
            return false;
        }

        Utente giudice = trovaGiudice(h);

        Valutazione v = sub.registraValutazione(giudizio, punteggio, giudice);
        if (v == null) {
            return false;
        }

        repoValutazione.salva(v);
        return true;
    }

    private Utente trovaGiudice(Hackathon h) {
        for (Incarico inc : h.getStaff()) {
            if (inc.getRuolo() == RuoloStaff.Giudice) {
                return inc.getUtente();
            }
        }
        return null;
    }
}
