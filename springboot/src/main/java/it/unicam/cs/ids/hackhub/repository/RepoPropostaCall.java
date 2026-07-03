package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.PropostaCall;
import it.unicam.cs.ids.hackhub.entity.Team;

import java.util.List;

// Pure Fabrication: persistenza delle PropostaCall. Solo i control vi accedono.
public interface RepoPropostaCall {

    List<PropostaCall> propostePerTeam(Team team);

    void salva(PropostaCall proposta);
}
