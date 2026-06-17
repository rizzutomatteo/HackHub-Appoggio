package it.unicam.cs.ids.hackhub.state;

public abstract class Stato {

    public abstract boolean iscrizioneConsentita();

    public abstract boolean sottomissioneConsentita();

    public abstract boolean valutazioneConsentita();

    // Transizioni del ciclo di vita (pattern State): per default la transizione
    // non e' consentita dallo stato corrente e restituisce null. Ogni stato concreto
    // ridefinisce solo la transizione ammessa, creando e restituendo il nuovo stato.
    public Stato avvia() {
        return null;
    }

    public Stato iniziaValutazione() {
        return null;
    }

    public Stato concludi() {
        return null;
    }
}
