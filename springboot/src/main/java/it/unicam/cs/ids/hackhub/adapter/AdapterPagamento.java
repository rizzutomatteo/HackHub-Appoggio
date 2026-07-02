package it.unicam.cs.ids.hackhub.adapter;

import org.springframework.stereotype.Component;

import it.unicam.cs.ids.hackhub.entity.Team;

// Pattern Adapter — «adapter» (Adapter): adatta l'API esterna SistemaPagamento (Adaptee) alla porta
// PaymentGateway (Target) di cui la piattaforma ha bisogno. Il Client (ServicePagamento) usa la porta;
// l'Adapter traduce erogaPremio(team, importo) nella chiamata eroga(importo) del sistema esterno
// (il beneficiario e' tracciato dal dominio; l'API esterna richiede solo l'importo).
@Component
public class AdapterPagamento implements PaymentGateway {

    private final SistemaPagamento sistemaPagamento;

    public AdapterPagamento(SistemaPagamento sistemaPagamento) {
        this.sistemaPagamento = sistemaPagamento;
    }

    @Override
    public String erogaPremio(Team team, long importo) {
        return sistemaPagamento.eroga(importo);
    }
}
