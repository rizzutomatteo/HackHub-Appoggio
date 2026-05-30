package it.unicam.cs.ids.hackhub.state;

public class InCorso extends Stato {

    @Override
    public boolean iscrizioneConsentita() {
        return false;
    }

    @Override
    public boolean sottomissioneConsentita() {
        return true;
    }

    @Override
    public boolean valutazioneConsentita() {
        return false;
    }
}
