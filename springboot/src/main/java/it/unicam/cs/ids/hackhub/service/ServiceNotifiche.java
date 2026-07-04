package it.unicam.cs.ids.hackhub.service;

import org.springframework.stereotype.Service;

import it.unicam.cs.ids.hackhub.observer.Notifica;
import it.unicam.cs.ids.hackhub.observer.Observer;
import it.unicam.cs.ids.hackhub.observer.Subject;

import java.util.ArrayList;
import java.util.List;

// ConcreteSubject del pattern Observer. Da iter.3 notifica un payload generico Notifica
// (Invito, RichiestaSupporto, PropostaCall). La registrazione degli osservatori avviene
// altrove (fuori dallo scope di questo scheletro di dominio).
@Service
public class ServiceNotifiche implements Subject {

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
    public void notifica(Notifica notifica) {
        if (notifica == null) {
            return;
        }
        for (Observer o : osservatori) {
            o.aggiorna(notifica);
        }
    }
}
