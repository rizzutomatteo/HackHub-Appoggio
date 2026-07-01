package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;

import java.util.List;

public interface RepoHackathon {

    void salva(Hackathon h);

    // UC21: tutti gli hackathon (per la consultazione pubblica del Visitatore).
    List<Hackathon> tutti();
}
