package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.entity.Hackathon;
import it.unicam.cs.ids.hackhub.entity.Sottomissione;

import java.util.List;

public interface RepoSottomissione {

    void salva(Sottomissione sub);

    List<Sottomissione> sottomissioniDi(Hackathon h);
}
