package it.unicam.cs.ids.hackhub.service;

import org.springframework.stereotype.Service;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.RichiestaSupporto;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoIscrizione;
import it.unicam.cs.ids.hackhub.repository.RepoRichiestaSupporto;

import java.util.List;

// Control (GRASP Controller) del supporto (UC11 Richiede Supporto, UC12 Visualizza Richieste).
// Creator della RichiestaSupporto. Usa l'Observer (ServiceNotifiche) per avvisare i Mentori.
@Service
public class ServiceSupporto {

    private final RepoRichiestaSupporto repoRichiesta;
    private final RepoIscrizione repoIscrizione;
    private final ServiceNotifiche serviceNotifiche;

    public ServiceSupporto(RepoRichiestaSupporto repoRichiesta,
                           RepoIscrizione repoIscrizione,
                           ServiceNotifiche serviceNotifiche) {
        this.repoRichiesta = repoRichiesta;
        this.repoIscrizione = repoIscrizione;
        this.serviceNotifiche = serviceNotifiche;
    }

    // UC11: apre una richiesta di supporto se il team e' iscritto e l'hackathon e' "in corso",
    // e non esiste gia' una richiesta aperta; poi notifica i Mentori (Observer).
    public boolean richiediSupporto(Team team, Hackathon hackathon, String messaggio) {
        if (team == null || hackathon == null || messaggio == null || messaggio.isEmpty()) {
            return false;
        }
        if (!repoIscrizione.iscrizioneInCorso(team, hackathon)) {
            return false;   // team non iscritto o hackathon non "in corso" (UC11 1a)
        }
        if (repoRichiesta.esisteRichiestaAperta(team, hackathon)) {
            return false;   // richiesta gia' aperta per questo team (UC11 3a)
        }

        RichiestaSupporto r = new RichiestaSupporto(team, hackathon, messaggio);
        repoRichiesta.salva(r);
        serviceNotifiche.notifica(r);
        return true;
    }

    // UC12: sola lettura. Scope via Incarico (le richieste degli hackathon del Mentore).
    public List<RichiestaSupporto> richiesteDelMentore(Utente mentore) {
        return repoRichiesta.trovaPerHackathonDelMentore(mentore);
    }
}
