package it.unicam.cs.ids.hackhub.state;

public class Concluso extends Stato {

    @Override
    public boolean iscrizioneConsentita() {
        return false;
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
