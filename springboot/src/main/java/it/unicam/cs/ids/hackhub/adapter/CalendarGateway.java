package it.unicam.cs.ids.hackhub.adapter;

import it.unicam.cs.ids.hackhub.entity.PropostaCall;

import java.time.LocalDateTime;

// Pattern Adapter — «interface» (Target/porta): la piattaforma dipende SOLO da questa porta
// (Dependency Inversion). L'accesso al Calendar esterno passa esclusivamente da qui.
// riservaSlot = prenotazione tentativa (UC13) -> riferimentoPrenotazione o null;
// confermaPrenotazione = conferma (UC14).
public interface CalendarGateway {

    String riservaSlot(LocalDateTime slot);

    boolean confermaPrenotazione(PropostaCall proposta);
}
