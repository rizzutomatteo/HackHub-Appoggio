package it.unicam.cs.ids.hackhub.adapter;

import it.unicam.cs.ids.hackhub.entity.Team;

// Pattern Adapter — «interface» (Target/porta): la piattaforma dipende SOLO da questa porta
// (Dependency Inversion), gemella di CalendarGateway. L'accesso al Sistema di Pagamento esterno
// passa esclusivamente da qui (UC16). Contratto sincrono (§1.5):
//   - riferimentoPagamento (stringa non vuota) -> erogazione completata;
//   - stringa vuota            -> erogazione rifiutata (fondi/transazione respinta, UC16 3b);
//   - null                     -> Sistema di Pagamento non disponibile (UC16 3a).
public interface PaymentGateway {

    String erogaPremio(Team team, long importo);
}
