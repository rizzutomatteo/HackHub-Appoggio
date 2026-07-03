package it.unicam.cs.ids.hackhub.adapter;

import java.util.UUID;

/**
 * Fallback in-memory dell'Adaptee {@link SistemaPagamento} (nessuna rete).
 *
 * <p>Serve a far partire l'applicazione SENZA credenziali Stripe: la configurazione
 * {@code EsterniConfig} la sceglie quando non e' impostata una secret key di test.
 * Non e' annotata con {@code @Component}: la istanzia la configurazione.</p>
 *
 * <p>eroga -> ritorna sempre un riferimento simulato "ref-&lt;uuid&gt;" (successo simulato,
 * stringa non vuota).</p>
 */
public class SistemaPagamentoInMemory implements SistemaPagamento {

    @Override
    public String eroga(long importo) {
        return "ref-" + UUID.randomUUID();
    }
}
