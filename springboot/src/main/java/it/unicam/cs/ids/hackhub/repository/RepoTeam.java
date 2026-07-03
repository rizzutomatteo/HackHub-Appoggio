package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Team;

public interface RepoTeam {

    boolean esisteNome(String nome);

    void salva(Team t);
}
