package it.unicam.cs.ids.hackhub.entity;

import it.unicam.cs.ids.hackhub.observer.Notifica;
import it.unicam.cs.ids.hackhub.observer.Observer;

public class Utente implements Observer {

    private final String nome;
    private final String email;
    private final String password;

    public Utente(String nome, String email) {
        this(nome, email, null);
    }

    // UC19: registrazione con credenziali (autenticazione "sottile", D4).
    public Utente(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    // Pattern Observer: l'Utente e' notificato (inviti, richieste di supporto, proposte di call, segnalazioni).
    // Da iter.3 il payload e' il marker Notifica. La consegna all'interfaccia utente e' demandata
    // allo strato di presentazione (fuori dallo scope di questo scheletro di dominio).
    @Override
    public void aggiorna(Notifica notifica) {
    }

    // UC20: verifica "sottile" della password (nessun hashing: fuori scope, D4).
    public boolean verificaPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
