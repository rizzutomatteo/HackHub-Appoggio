package it.unicam.cs.ids.hackhub.entity;

import it.unicam.cs.ids.hackhub.observer.Notifica;

import java.time.LocalDateTime;

// UC13-UC15: proposta di call di un Mentore (proponente) verso un Team (destinatario),
// in risposta a una RichiestaSupporto. riferimentoPrenotazione = id dello slot sul Calendar
// esterno (prenotazione delegata via Adapter). Realizza Notifica (Observer). stato e' un enum.
public class PropostaCall implements Notifica {

    private final Utente proponente;
    private final Team destinatario;
    private final RichiestaSupporto richiesta;
    private final LocalDateTime dataOraProposta;
    private final String riferimentoPrenotazione;
    private StatoCall stato;

    public PropostaCall(Utente proponente,
                        Team destinatario,
                        RichiestaSupporto richiesta,
                        LocalDateTime dataOraProposta,
                        String riferimentoPrenotazione) {
        this.proponente = proponente;
        this.destinatario = destinatario;
        this.richiesta = richiesta;
        this.dataOraProposta = dataOraProposta;
        this.riferimentoPrenotazione = riferimentoPrenotazione;
        this.stato = StatoCall.Proposta;
    }

    public void accetta() {
        this.stato = StatoCall.Accettata;
    }

    public void rifiuta() {
        this.stato = StatoCall.Rifiutata;
    }

    public Utente getProponente() {
        return proponente;
    }

    public Team getDestinatario() {
        return destinatario;
    }

    public RichiestaSupporto getRichiesta() {
        return richiesta;
    }

    public LocalDateTime getDataOraProposta() {
        return dataOraProposta;
    }

    public String getRiferimentoPrenotazione() {
        return riferimentoPrenotazione;
    }

    public StatoCall getStato() {
        return stato;
    }
}
