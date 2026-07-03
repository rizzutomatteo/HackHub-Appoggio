package it.unicam.cs.ids.hackhub.adapter;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sistema esterno REALE per il Calendar: implementazione dell'Adaptee {@link Calendar}
 * basata sulla Google Calendar API (com.google.api.services.calendar).
 *
 * <p>Fa parte del pattern Adapter: e' l'Adaptee concreto raggiunto solo tramite
 * {@code AdapterCalendar}. Non e' annotata con {@code @Component}: viene istanziata
 * dalla configurazione {@code EsterniConfig} solo quando sono presenti le credenziali.</p>
 *
 * <p>Autenticazione tramite <b>service account</b> (chiave JSON): il calendario da usare
 * deve essere condiviso con l'email del service account (permesso "Apportare modifiche
 * agli eventi"). Lo scope richiesto e' {@link CalendarScopes#CALENDAR}.</p>
 *
 * <p>Contratto dei valori di ritorno (§ pattern Adapter):
 * riferimento non vuoto = successo; null = servizio non disponibile / errore.
 * Le eccezioni ({@link IOException}, {@link GeneralSecurityException}) non vengono
 * propagate: log + ritorno null.</p>
 */
public class GoogleCalendar implements Calendar {

    private static final Logger LOG = Logger.getLogger(GoogleCalendar.class.getName());

    /** Durata di default dell'evento creato per uno slot di call. */
    private static final int DURATA_EVENTO_MINUTI = 30;

    private final String credentialsPath;
    private final String calendarId;

    /** Client Google Calendar, inizializzato lazy al primo utilizzo. */
    private com.google.api.services.calendar.Calendar client;

    /**
     * @param credentialsPath path del file JSON della chiave del service account
     * @param calendarId      id del calendario da usare (es. "primary" o l'indirizzo del calendario)
     */
    public GoogleCalendar(String credentialsPath, String calendarId) {
        this.credentialsPath = credentialsPath;
        this.calendarId = calendarId;
    }

    /**
     * Inizializza (lazy) il client Google Calendar costruendolo con trasporto HTTP,
     * {@link GsonFactory} e le credenziali del service account.
     *
     * @return il client, oppure null se l'inizializzazione fallisce.
     */
    private synchronized com.google.api.services.calendar.Calendar client() {
        if (client != null) {
            return client;
        }
        try (InputStream in = new FileInputStream(credentialsPath)) {
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(Collections.singletonList(CalendarScopes.CALENDAR));
            this.client = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, new HttpCredentialsAdapter(credentials))
                    .setApplicationName("HackHub")
                    .build();
            return client;
        } catch (IOException | GeneralSecurityException e) {
            LOG.log(Level.WARNING, "Google Calendar non inizializzabile: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Crea un evento di ~30 minuti a partire da {@code slot} sul calendario configurato.
     *
     * @return l'id dell'evento creato (riferimentoPrenotazione) se ok; null in caso di errore
     *         o servizio non disponibile.
     */
    @Override
    public String riserva(LocalDateTime slot) {
        com.google.api.services.calendar.Calendar cal = client();
        if (cal == null) {
            return null;
        }
        try {
            ZoneId zona = ZoneId.systemDefault();
            ZonedDateTime inizio = slot.atZone(zona);
            ZonedDateTime fine = inizio.plusMinutes(DURATA_EVENTO_MINUTI);

            Event evento = new Event()
                    .setSummary("HackHub - Call")
                    .setDescription("Slot di call prenotato tramite HackHub")
                    .setStart(new EventDateTime()
                            .setDateTime(new com.google.api.client.util.DateTime(Date.from(inizio.toInstant())))
                            .setTimeZone(zona.getId()))
                    .setEnd(new EventDateTime()
                            .setDateTime(new com.google.api.client.util.DateTime(Date.from(fine.toInstant())))
                            .setTimeZone(zona.getId()));

            Event creato = cal.events().insert(calendarId, evento).execute();
            if (creato == null || creato.getId() == null || creato.getId().isEmpty()) {
                return null;
            }
            return creato.getId();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Riserva slot fallita su Google Calendar: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Verifica che l'evento esista (o sia confermato) rileggendolo dal calendario.
     *
     * @return true se l'evento esiste ed e' valido; false/errore altrimenti.
     */
    @Override
    public boolean conferma(String riferimentoPrenotazione) {
        if (riferimentoPrenotazione == null || riferimentoPrenotazione.isEmpty()) {
            return false;
        }
        com.google.api.services.calendar.Calendar cal = client();
        if (cal == null) {
            return false;
        }
        try {
            Event evento = cal.events().get(calendarId, riferimentoPrenotazione).execute();
            if (evento == null) {
                return false;
            }
            // "cancelled" = evento annullato -> non confermato; qualsiasi altro stato = ok.
            String stato = evento.getStatus();
            return stato == null || !"cancelled".equalsIgnoreCase(stato);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Conferma prenotazione fallita su Google Calendar: " + e.getMessage(), e);
            return false;
        }
    }
}
