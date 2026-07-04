package it.unicam.cs.ids.hackhub.service;

import org.springframework.stereotype.Service;

import it.unicam.cs.ids.hackhub.entity.Invito;
import it.unicam.cs.ids.hackhub.entity.Iscrizione;
import it.unicam.cs.ids.hackhub.entity.StatoInvito;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoInvito;
import it.unicam.cs.ids.hackhub.repository.RepoIscrizione;
import it.unicam.cs.ids.hackhub.repository.RepoTeam;
import it.unicam.cs.ids.hackhub.repository.RepoUtente;

@Service
public class ServiceInvito {

    private final RepoInvito repoInvito;
    private final RepoUtente repoUtente;
    private final RepoTeam repoTeam;
    private final RepoIscrizione repoIscrizione;
    private final ServiceNotifiche serviceNotifiche;

    public ServiceInvito(RepoInvito repoInvito,
                         RepoUtente repoUtente,
                         RepoTeam repoTeam,
                         RepoIscrizione repoIscrizione,
                         ServiceNotifiche serviceNotifiche) {
        this.repoInvito = repoInvito;
        this.repoUtente = repoUtente;
        this.repoTeam = repoTeam;
        this.repoIscrizione = repoIscrizione;
        this.serviceNotifiche = serviceNotifiche;
    }

    public boolean invita(Team team, Utente utenteInvitato) {
        if (team == null || utenteInvitato == null) {
            return false;
        }
        if (repoInvito.esisteInvitoPendente(team, utenteInvitato)) {
            return false;
        }

        Invito inv = new Invito(team, utenteInvitato);
        repoInvito.salva(inv);
        serviceNotifiche.notifica(inv);
        return true;
    }

    public boolean accetta(Invito invito) {
        if (invito == null) {
            return false;
        }
        if (invito.getStato() != StatoInvito.Pendente) {
            return false;
        }

        Utente utente = invito.getUtenteInvitato();
        if (repoUtente.appartieneAdUnTeam(utente)) {
            return false;
        }

        Team team = invito.getTeam();
        if (!team.dimensioneCompatibile(dimensioneMaxConsentita(team))) {
            return false;
        }

        team.aggiungiMembro(utente);
        repoTeam.salva(team);
        invito.accetta();
        repoInvito.salva(invito);
        return true;
    }

    // Vincolo di dimensione (UC10 passo 5): l'aggiunta non deve superare il
    // dimensioneMaxTeam piu' restrittivo fra gli hackathon a cui il team e' iscritto.
    // Senza iscrizioni il team non ha alcun vincolo di dimensione.
    private int dimensioneMaxConsentita(Team team) {
        int max = Integer.MAX_VALUE;
        for (Iscrizione i : repoIscrizione.iscrizioniDi(team)) {
            int d = i.getHackathon().getDimensioneMaxTeam();
            if (d < max) {
                max = d;
            }
        }
        return max;
    }
}
