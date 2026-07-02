package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Pagamento;

// Pure Fabrication: persistenza dei Pagamento. Solo i control vi accedono.
public interface RepoPagamento {

    // Idempotenza UC16 (2a): esiste gia' un Pagamento "Completato" per l'hackathon?
    boolean esistePagamentoCompletato(Hackathon hackathon);

    void salva(Pagamento pag);
}
