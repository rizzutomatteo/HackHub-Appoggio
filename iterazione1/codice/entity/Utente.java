package it.unicam.cs.ids.hackhub.entity;

import it.unicam.cs.ids.hackhub.observer.Observer;

public class Utente implements Observer {

    private final String nome;
    private final String email;

    public Utente(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Pattern Observer: l'Utente e' notificato dei nuovi inviti (UC09). La consegna
    // della notifica all'interfaccia utente e' demandata allo strato di presentazione
    // (fuori dallo scope di questo scheletro di dominio, come l'autenticazione).
    @Override
    public void aggiorna(Invito invito) {
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
