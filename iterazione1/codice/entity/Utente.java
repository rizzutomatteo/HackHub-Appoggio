package it.unicam.cs.ids.hackhub.entity;

public class Utente {

    private final String nome;
    private final String email;

    public Utente(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
