package it.unicam.cs.ids.hackhub.state;

public class InIscrizione extends Stato {

    @Override
    public boolean iscrizioneConsentita() {
        return true;
    }

    @Override
    public boolean sottomissioneConsentita() {
        return false;
    }

    @Override
    public boolean valutazioneConsentita() {
        return false;
    }
}
