package it.unicam.cs.ids.hackhub.entity;

// Stato di Pagamento: enum semplice (NON pattern State). Inviato e' lo stato iniziale/transitorio;
// l'idempotenza di UC16 guarda il "Completato".
public enum StatoPagamento {
    Inviato,
    Completato,
    Fallito
}
