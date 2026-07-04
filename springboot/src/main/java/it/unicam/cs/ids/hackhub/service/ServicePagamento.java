package it.unicam.cs.ids.hackhub.service;

import org.springframework.stereotype.Service;

import it.unicam.cs.ids.hackhub.adapter.PaymentGateway;
import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Pagamento;
import it.unicam.cs.ids.hackhub.entity.StatoPagamento;
import it.unicam.cs.ids.hackhub.entity.Team;
import it.unicam.cs.ids.hackhub.repository.RepoHackathon;
import it.unicam.cs.ids.hackhub.repository.RepoPagamento;

// Control (GRASP Controller) del pagamento (UC16 Paga Montepremi). Creator del Pagamento.
// Dipende SOLO dalla porta PaymentGateway (pattern Adapter, gemello di CalendarGateway): il Sistema
// di Pagamento esterno e' raggiunto solo via adapter. Information Expert: vincitore/montepremi letti
// dall'Hackathon. Idempotenza (2a): al piu' un Pagamento "Completato" per hackathon.
@Service
public class ServicePagamento {

    private final RepoPagamento repoPagamento;
    private final PaymentGateway paymentGateway;
    private final RepoHackathon repoHackathon;

    public ServicePagamento(RepoPagamento repoPagamento,
                            PaymentGateway paymentGateway,
                            RepoHackathon repoHackathon) {
        this.repoPagamento = repoPagamento;
        this.paymentGateway = paymentGateway;
        this.repoHackathon = repoHackathon;
    }

    // UC16: eroga il montepremi al team vincitore di un hackathon "Concluso", una sola volta.
    // Nota (D3): il vincitore e' impostato solo da UC08, che pone anche lo stato a "Concluso";
    // percio' "vincitore != null" implica "Concluso" (la guardia 1a copre entrambe le condizioni).
    public boolean paga(Hackathon hackathon) {
        if (hackathon == null) {
            return false;
        }
        Team teamVincitore = hackathon.getVincitore();
        if (teamVincitore == null) {
            return false;   // hackathon non "Concluso" o senza vincitore (UC16 1a)
        }
        if (repoPagamento.esistePagamentoCompletato(hackathon)) {
            return false;   // premio gia' erogato (UC16 2a, idempotenza)
        }

        long importo = hackathon.getMontepremi();
        String riferimento = paymentGateway.erogaPremio(teamVincitore, importo);

        if (riferimento == null) {
            return false;   // Sistema di Pagamento non disponibile (UC16 3a): nessun Pagamento registrato
        }
        if (riferimento.isEmpty()) {
            // erogazione rifiutata (UC16 3b): si registra il tentativo come "Fallito"
            Pagamento fallito = new Pagamento(hackathon, teamVincitore, importo, null, StatoPagamento.Fallito);
            repoPagamento.salva(fallito);
            return false;
        }

        // erogazione completata: riferimento = riferimentoPagamento (UC16 4-5)
        Pagamento completato = new Pagamento(hackathon, teamVincitore, importo, riferimento, StatoPagamento.Completato);
        repoPagamento.salva(completato);
        repoHackathon.salva(hackathon);   // persiste l'aggregato a chiusura della catena del valore
        return true;
    }
}
