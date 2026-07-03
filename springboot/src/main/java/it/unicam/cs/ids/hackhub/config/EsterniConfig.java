package it.unicam.cs.ids.hackhub.config;

import it.unicam.cs.ids.hackhub.adapter.Calendar;
import it.unicam.cs.ids.hackhub.adapter.CalendarInMemory;
import it.unicam.cs.ids.hackhub.adapter.GoogleCalendar;
import it.unicam.cs.ids.hackhub.adapter.SistemaPagamento;
import it.unicam.cs.ids.hackhub.adapter.SistemaPagamentoInMemory;
import it.unicam.cs.ids.hackhub.adapter.StripeSistemaPagamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.logging.Logger;

/**
 * Configurazione dei sistemi esterni (Adaptee) del pattern Adapter.
 *
 * <p>Sceglie l'implementazione concreta da iniettare negli Adapter ({@code AdapterCalendar},
 * {@code AdapterPagamento}) in base alle property applicative:</p>
 * <ul>
 *   <li>Calendar: {@link GoogleCalendar} se sono presenti credenziali valide, altrimenti il
 *       fallback {@link CalendarInMemory};</li>
 *   <li>SistemaPagamento: {@link StripeSistemaPagamento} se e' presente una secret key di test
 *       ({@code sk_test_...}), altrimenti il fallback {@link SistemaPagamentoInMemory}.</li>
 * </ul>
 *
 * <p>I bean qui definiti soddisfano i costruttori degli Adapter (che sono {@code @Component}).
 * Le implementazioni concrete NON sono annotate con {@code @Component}: le istanzia questa
 * configurazione, cosi' la scelta e' centralizzata e guidata dalle property.</p>
 */
@Configuration
public class EsterniConfig {

    private static final Logger LOG = Logger.getLogger(EsterniConfig.class.getName());

    /** Path del file JSON del service account Google (vuoto = usa il fallback in-memory). */
    @Value("${google.calendar.credentials:}")
    private String googleCreds;

    /** Id del calendario Google da usare (default "primary"). */
    @Value("${google.calendar.id:primary}")
    private String calendarId;

    /** Secret key di TEST di Stripe (vuota o non sk_test_ = usa il fallback in-memory). */
    @Value("${stripe.api-key:}")
    private String stripeKey;

    /**
     * Adaptee Calendar: Google Calendar reale se le credenziali sono presenti e il file esiste,
     * altrimenti il fallback in-memory.
     */
    @Bean
    public Calendar calendar() {
        if (googleCreds != null && !googleCreds.isBlank() && new File(googleCreds).exists()) {
            LOG.info("Calendar: uso Google Calendar reale (credenziali: " + googleCreds
                    + ", calendarId: " + calendarId + ")");
            return new GoogleCalendar(googleCreds, calendarId);
        }
        LOG.info("Calendar: uso fallback in-memory (nessuna credenziale Google configurata)");
        return new CalendarInMemory();
    }

    /**
     * Adaptee SistemaPagamento: Stripe reale (modalita' test) se la key inizia con "sk_test_",
     * altrimenti il fallback in-memory.
     */
    @Bean
    public SistemaPagamento sistemaPagamento() {
        if (stripeKey != null && stripeKey.startsWith("sk_test_")) {
            LOG.info("SistemaPagamento: uso Stripe reale (modalita' test)");
            return new StripeSistemaPagamento(stripeKey);
        }
        LOG.info("SistemaPagamento: uso fallback in-memory (nessuna secret key di test Stripe configurata)");
        return new SistemaPagamentoInMemory();
    }
}
