package it.unicam.cs.ids.hackhub.adapter;

import java.time.LocalDateTime;

// Pattern Adapter — «sistema esterno» (Adaptee): API del servizio esterno Calendar.
// Raggiunto SOLO tramite AdapterCalendar; i service non dipendono da questa API.
// riserva = prenotazione tentativa (restituisce il riferimentoPrenotazione, null se non disponibile);
// conferma = conferma definitiva della prenotazione.
public interface Calendar {

    String riserva(LocalDateTime slot);

    boolean conferma(String riferimentoPrenotazione);
}
