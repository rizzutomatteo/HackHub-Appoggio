package it.unicam.cs.ids.hackhub.entity;

import it.unicam.cs.ids.hackhub.observer.Notifica;
import it.unicam.cs.ids.hackhub.observer.Observer;

public class Utente implements Observer {

    private final String nome;
    private final String email;

    public Utente(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Pattern Observer: l'Utente e' notificato (inviti, richieste di supporto, proposte di call).
    // Da iter.3 il payload e' il marker Notifica. La consegna all'interfaccia utente e' demandata
    // allo strato di presentazione (fuori dallo scope di questo scheletro di dominio).
    @Override
    public void aggiorna(Notifica notifica) {
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
