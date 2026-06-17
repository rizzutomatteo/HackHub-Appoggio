package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.entity.Invito;
import it.unicam.cs.ids.hackhub.observer.Observer;
import it.unicam.cs.ids.hackhub.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class ServizioNotifiche implements Subject {

    private final List<Observer> osservatori = new ArrayList<>();

    @Override
    public void registra(Observer o) {
        if (o != null && !osservatori.contains(o)) {
            osservatori.add(o);
        }
    }

    @Override
    public void rimuovi(Observer o) {
        osservatori.remove(o);
    }

    @Override
    public void notifica(Invito invito) {
        if (invito == null) {
            return;
        }
        for (Observer o : osservatori) {
            o.aggiorna(invito);
        }
    }
}
