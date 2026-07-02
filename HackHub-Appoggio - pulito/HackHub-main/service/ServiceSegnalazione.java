package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Incarico;
import it.unicam.cs.ids.hackhub.entity.RuoloStaff;
import it.unicam.cs.ids.hackhub.entity.Segnalazione;
import it.unicam.cs.ids.hackhub.entity.StatoSegnalazione;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoSegnalazione;

import java.util.List;

// Control (GRASP Controller) della governance (UC17 Segnala Team, UC18 Gestisci Segnalazione).
// Creator della Segnalazione. Usa l'Observer (ServiceNotifiche) per avvisare l'Organizzatore.
// Information Expert / scope via Incarico: il Mentore segnala solo i team dei propri hackathon.
public class ServiceSegnalazione {

    private final RepoSegnalazione repoSegnalazione;
    private final ServiceNotifiche serviceNotifiche;

    public ServiceSegnalazione(RepoSegnalazione repoSegnalazione, ServiceNotifiche serviceNotifiche) {
        this.repoSegnalazione = repoSegnalazione;
        this.serviceNotifiche = serviceNotifiche;
    }

    // UC17: apre una segnalazione se il Mentore e' assegnato all'hackathon e non ne esiste gia' una
    // aperta per lo stesso team; poi notifica l'Organizzatore (Observer).
    public boolean segnala(Utente mentore, Team team, Hackathon hackathon, String descrizione) {
        if (mentore == null || team == null || hackathon == null || descrizione == null || descrizione.isEmpty()) {
            return false;
        }
        if (!mentoreAssegnato(mentore, hackathon)) {
            return false;   // fuori competenza (UC17 1a)
        }
        if (repoSegnalazione.esisteSegnalazioneAperta(mentore, team, hackathon)) {
            return false;   // segnalazione gia' aperta per questo team (UC17 3a)
        }

        Segnalazione seg = new Segnalazione(mentore, team, hackathon, descrizione);
        repoSegnalazione.salva(seg);
        serviceNotifiche.notifica(seg);
        return true;
    }

    // UC18: sola lettura. Scope via Incarico (le segnalazioni degli hackathon dell'Organizzatore).
    public List<Segnalazione> segnalazioniDellOrganizzatore(Utente organizzatore) {
        return repoSegnalazione.trovaPerHackathonDellOrganizzatore(organizzatore);
    }

    // UC18: l'Organizzatore registra la decisione del caso su una segnalazione ancora "Aperta".
    public boolean gestisci(Segnalazione segnalazione, String decisione) {
        if (segnalazione == null || decisione == null || decisione.isEmpty()) {
            return false;
        }
        if (segnalazione.getStato() != StatoSegnalazione.Aperta) {
            return false;   // gia' gestita/archiviata (UC18 3a)
        }

        segnalazione.gestisci(decisione);
        repoSegnalazione.salva(segnalazione);
        return true;
    }

    // UC18 3b: l'Organizzatore archivia la segnalazione senza provvedimenti.
    public boolean archivia(Segnalazione segnalazione) {
        if (segnalazione == null) {
            return false;
        }
        segnalazione.archivia();
        repoSegnalazione.salva(segnalazione);
        return true;
    }

    // Scope via Incarico: il Mentore deve essere assegnato (ruolo = Mentore) all'hackathon.
    private boolean mentoreAssegnato(Utente mentore, Hackathon hackathon) {
        for (Incarico inc : hackathon.getStaff()) {
            if (inc.getRuolo() == RuoloStaff.Mentore && inc.getUtente().equals(mentore)) {
                return true;
            }
        }
        return false;
    }
}
