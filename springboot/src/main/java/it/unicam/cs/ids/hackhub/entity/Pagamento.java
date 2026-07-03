package it.unicam.cs.ids.hackhub.entity;

import java.time.LocalDate;

// UC16: erogazione del montepremi al team vincitore, delegata al Sistema di Pagamento (pattern Adapter).
// Entita' creata e letta (nessuna logica di dominio). stato e' un enum (Inviato/Completato/Fallito),
// NON un pattern State. Idempotenza: al piu' un Pagamento "Completato" per hackathon
// (RepoPagamento.esistePagamentoCompletato). riferimentoPagamento = id transazione esterna (null se Fallito).
public class Pagamento {

    private final Hackathon hackathon;          // hackathon di cui si eroga il premio
    private final Team beneficiario;            // team vincitore
    private final long importo;
    private final LocalDate dataPagamento;
    private final String riferimentoPagamento;
    private final StatoPagamento stato;

    public Pagamento(Hackathon hackathon,
                     Team beneficiario,
                     long importo,
                     String riferimentoPagamento,
                     StatoPagamento stato) {
        this.hackathon = hackathon;
        this.beneficiario = beneficiario;
        this.importo = importo;
        this.riferimentoPagamento = riferimentoPagamento;
        this.dataPagamento = LocalDate.now();
        this.stato = stato;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public Team getBeneficiario() {
        return beneficiario;
    }

    public long getImporto() {
        return importo;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public String getRiferimentoPagamento() {
        return riferimentoPagamento;
    }

    public StatoPagamento getStato() {
        return stato;
    }
}
