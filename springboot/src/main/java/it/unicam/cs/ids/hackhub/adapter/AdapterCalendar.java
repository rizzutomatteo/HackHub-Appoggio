package it.unicam.cs.ids.hackhub.adapter;

import org.springframework.stereotype.Component;

import it.unicam.cs.ids.hackhub.entity.PropostaCall;

import java.time.LocalDateTime;

// Pattern Adapter — «adapter» (Adapter): adatta l'API esterna Calendar (Adaptee) alla porta
// CalendarGateway (Target) di cui la piattaforma ha bisogno. Il Client (ServiceCall) usa la
// porta; l'Adapter traduce le chiamate verso il Calendar. confermaPrenotazione estrae dalla
// PropostaCall il riferimentoPrenotazione ottenuto in fase di riservaSlot.
@Component
public class AdapterCalendar implements CalendarGateway {

    private final Calendar calendar;

    public AdapterCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public String riservaSlot(LocalDateTime slot) {
        return calendar.riserva(slot);
    }

    @Override
    public boolean confermaPrenotazione(PropostaCall proposta) {
        return calendar.conferma(proposta.getRiferimentoPrenotazione());
    }
}
