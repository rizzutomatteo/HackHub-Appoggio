package it.unicam.cs.ids.hackhub.observer;

// Pattern Observer: contratto del soggetto osservabile. Da iter.3 il payload di notifica
// e' il marker Notifica (prima Invito).
public interface Subject {

    void registra(Observer o);

    void rimuovi(Observer o);

    void notifica(Notifica notifica);
}
