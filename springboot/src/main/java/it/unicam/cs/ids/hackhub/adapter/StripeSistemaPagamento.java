package it.unicam.cs.ids.hackhub.adapter;

import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sistema esterno REALE per i pagamenti: implementazione dell'Adaptee {@link SistemaPagamento}
 * basata su stripe-java, in <b>modalita' TEST</b>.
 *
 * <p>Fa parte del pattern Adapter: e' l'Adaptee concreto raggiunto solo tramite
 * {@code AdapterPagamento}. Non e' annotata con {@code @Component}: viene istanziata
 * dalla configurazione {@code EsterniConfig} solo quando e' presente una secret key di test
 * (prefisso {@code sk_test_}).</p>
 *
 * <p><b>Nota modalita' test:</b> il pagamento e' simulato con la carta di test di Stripe
 * {@code pm_card_visa} (payment method di prova che va sempre a buon fine). Nessun addebito
 * reale avviene con una {@code sk_test_...}. Per simulare un rifiuto si puo' usare una carta
 * di test diversa (es. {@code pm_card_visa_chargeDeclined}).</p>
 *
 * <p>Contratto dei valori di ritorno (§ pattern Adapter):
 * <ul>
 *   <li>id del PaymentIntent (stringa non vuota) -> erogazione completata ("succeeded");</li>
 *   <li>stringa vuota "" -> pagamento rifiutato (carta rifiutata / requires_payment_method);</li>
 *   <li>null -> Sistema di Pagamento non disponibile (errore di rete/API non legato alla carta).</li>
 * </ul></p>
 */
public class StripeSistemaPagamento implements SistemaPagamento {

    private static final Logger LOG = Logger.getLogger(StripeSistemaPagamento.class.getName());

    /**
     * @param apiKey secret key di TEST di Stripe (sk_test_...). Viene impostata su
     *               {@link Stripe#apiKey} (statica, globale per il processo).
     */
    public StripeSistemaPagamento(String apiKey) {
        Stripe.apiKey = apiKey;
    }

    /**
     * Crea (in modalita' test) un PaymentIntent confermato con la carta di test Visa.
     *
     * @param importo importo in EURO (verra' convertito in centesimi: importo * 100).
     * @return id del PaymentIntent se "succeeded"; "" se la carta e' rifiutata;
     *         null per errori di rete/API non legati alla carta.
     */
    @Override
    public String eroga(long importo) {
        try {
            Map<String, Object> params = new HashMap<>();
            // Stripe lavora nell'unita' minima della valuta: euro -> centesimi.
            params.put("amount", importo * 100L);
            params.put("currency", "eur");
            // Carta di test di Stripe (payment method di prova).
            params.put("payment_method", "pm_card_visa");
            // Conferma immediata del pagamento nella stessa chiamata.
            params.put("confirm", true);
            // Pagamento off_session (nessun cliente interattivo / redirect richiesto).
            params.put("off_session", true);

            PaymentIntent intent = PaymentIntent.create(params);

            String stato = intent.getStatus();
            if ("succeeded".equals(stato)) {
                return intent.getId();
            }
            // requires_payment_method -> la carta e' stata rifiutata: erogazione rifiutata.
            LOG.log(Level.INFO, "Pagamento non riuscito (stato Stripe: " + stato + ") -> rifiutato");
            return "";
        } catch (CardException e) {
            // Carta rifiutata (fondi insufficienti, carta declinata, ecc.) -> rifiutato.
            LOG.log(Level.INFO, "Carta rifiutata da Stripe: " + e.getMessage());
            return "";
        } catch (StripeException e) {
            // Errore di rete / API non legato alla carta -> servizio non disponibile.
            LOG.log(Level.WARNING, "Stripe non disponibile: " + e.getMessage(), e);
            return null;
        }
    }
}
