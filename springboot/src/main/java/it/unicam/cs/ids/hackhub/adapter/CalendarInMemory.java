package it.unicam.cs.ids.hackhub.adapter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fallback in-memory dell'Adaptee {@link Calendar} (nessuna rete).
 *
 * <p>Serve a far partire l'applicazione SENZA credenziali Google: la configurazione
 * {@code EsterniConfig} la sceglie quando non e' impostato il path del service account.
 * Non e' annotata con {@code @Component}: la istanzia la configurazione.</p>
 *
 * <p>riserva -> genera un UUID come riferimentoPrenotazione e memorizza lo slot;
 * conferma -> true se l'id e' presente in memoria.</p>
 */
public class CalendarInMemory implements Calendar {

    /** riferimentoPrenotazione (UUID) -> slot riservato. */
    private final Map<String, LocalDateTime> prenotazioni = new ConcurrentHashMap<>();

    @Override
    public String riserva(LocalDateTime slot) {
        String riferimento = UUID.randomUUID().toString();
        prenotazioni.put(riferimento, slot);
        return riferimento;
    }

    @Override
    public boolean conferma(String riferimentoPrenotazione) {
        return riferimentoPrenotazione != null && prenotazioni.containsKey(riferimentoPrenotazione);
    }
}
