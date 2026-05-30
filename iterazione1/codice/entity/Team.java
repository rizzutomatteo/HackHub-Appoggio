package it.unicam.cs.ids.hackhub.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

    private final String nome;
    private final List<Appartenenza> membri;

    public Team(String nome, Utente creatore) {
        this.nome = nome;
        this.membri = new ArrayList<>();
        this.membri.add(new Appartenenza(creatore, this, true));
    }

    public String getNome() {
        return nome;
    }

    public List<Appartenenza> getMembri() {
        return Collections.unmodifiableList(membri);
    }

    public int numeroMembri() {
        return membri.size();
    }
}
