package it.unicam.cs.ids.hackhub.service;

import org.springframework.stereotype.Service;

import it.unicam.cs.ids.hackhub.adapter.CalendarGateway;
import it.unicam.cs.ids.hackhub.entity.Incarico;
import it.unicam.cs.ids.hackhub.entity.PropostaCall;
import it.unicam.cs.ids.hackhub.entity.RichiestaSupporto;
import it.unicam.cs.ids.hackhub.entity.RuoloStaff;
import it.unicam.cs.ids.hackhub.entity.StatoCall;
import it.unicam.cs.ids.hackhub.entity.Utente;
import it.unicam.cs.ids.hackhub.repository.RepoPropostaCall;

import java.time.LocalDateTime;

// Control (GRASP Controller) delle call (UC13 Propone, UC14 Accetta, UC15 Rifiuta).
// Creator della PropostaCall. Dipende SOLO dalla porta CalendarGateway (pattern Adapter);
// usa l'Observer (ServiceNotifiche) per avvisare il team/il Mentore. Information Expert:
// lo stato della PropostaCall e' letto dall'entita' stessa.
@Service
public class ServiceCall {

    private final RepoPropostaCall repoProposta;
    private final CalendarGateway calendarGateway;
    private final ServiceNotifiche serviceNotifiche;

    public ServiceCall(RepoPropostaCall repoProposta,
                       CalendarGateway calendarGateway,
                       ServiceNotifiche serviceNotifiche) {
        this.repoProposta = repoProposta;
        this.calendarGateway = calendarGateway;
        this.serviceNotifiche = serviceNotifiche;
    }

    // UC13: solo un Mentore assegnato all'hackathon della richiesta puo' proporre; lo slot e'
    // prenotato "al volo" tramite l'Adapter (riservaSlot); poi si crea la PropostaCall e si notifica.
    public boolean proponiCall(Utente mentore, RichiestaSupporto richiesta, LocalDateTime slot) {
        if (mentore == null || richiesta == null || slot == null) {
            return false;
        }
        if (!mentoreAssegnato(mentore, richiesta)) {
            return false;   // fuori competenza (UC13 1a)
        }

        String riferimentoPrenotazione = calendarGateway.riservaSlot(slot);
        if (riferimentoPrenotazione == null) {
            return false;   // Calendar non disponibile o slot occupato (UC13 4a/4b)
        }

        PropostaCall proposta = new PropostaCall(mentore, richiesta.getTeam(), richiesta, slot, riferimentoPrenotazione);
        repoProposta.salva(proposta);
        serviceNotifiche.notifica(proposta);
        return true;
    }

    // UC14: conferma la prenotazione via Adapter, poi accetta e notifica il Mentore proponente.
    public boolean accettaProposta(PropostaCall proposta) {
        if (proposta == null) {
            return false;
        }
        if (proposta.getStato() != StatoCall.Proposta) {
            return false;   // gia' accettata/rifiutata (UC14 3a)
        }
        if (!calendarGateway.confermaPrenotazione(proposta)) {
            return false;   // Calendar non disponibile / slot non piu' disponibile (UC14 4a)
        }

        proposta.accetta();
        repoProposta.salva(proposta);
        serviceNotifiche.notifica(proposta);
        return true;
    }

    // UC15: nessuna interazione con Calendar; cambia lo stato a Rifiutata e notifica il proponente.
    public boolean rifiutaProposta(PropostaCall proposta) {
        if (proposta == null) {
            return false;
        }
        if (proposta.getStato() != StatoCall.Proposta) {
            return false;   // gia' accettata/rifiutata (UC15 3a)
        }

        proposta.rifiuta();
        repoProposta.salva(proposta);
        serviceNotifiche.notifica(proposta);
        return true;
    }

    // Scope via Incarico: il Mentore deve essere assegnato (ruolo = Mentore) all'hackathon della richiesta.
    private boolean mentoreAssegnato(Utente mentore, RichiestaSupporto richiesta) {
        for (Incarico inc : richiesta.getHackathon().getStaff()) {
            if (inc.getRuolo() == RuoloStaff.Mentore && inc.getUtente().equals(mentore)) {
                return true;
            }
        }
        return false;
    }
}
