package it.unicam.cs.ids.hackhub.adapter;

// Pattern Adapter — «sistema esterno» (Adaptee): API del servizio esterno Sistema di Pagamento.
// Raggiunto SOLO tramite AdapterPagamento; i service non dipendono da questa API.
// eroga = erogazione sincrona del premio: restituisce il riferimento di transazione se completata,
// stringa vuota se rifiutata, null se il servizio non e' disponibile.
public interface SistemaPagamento {

    String eroga(long importo);
}
