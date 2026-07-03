package it.unicam.cs.ids.hackhub.observer;

// Pattern Observer: contratto dell'osservatore. Da iter.3 il payload e' generalizzato al
// marker Notifica (prima era Invito), cosi' lo stesso Observer serve inviti (iter.2),
// richieste di supporto e proposte di call (iter.3).
public interface Observer {

    void aggiorna(Notifica notifica);
}
