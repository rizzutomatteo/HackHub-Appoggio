package it.unicam.cs.ids.hackhub.observer;

import it.unicam.cs.ids.hackhub.entity.Invito;

public interface Subject {

    void registra(Observer o);

    void rimuovi(Observer o);

    void notifica(Invito invito);
}
