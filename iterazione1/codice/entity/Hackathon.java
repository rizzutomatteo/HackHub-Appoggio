package it.unicam.cs.ids.hackhub.entity;

import it.unicam.cs.ids.hackhub.state.InIscrizione;
import it.unicam.cs.ids.hackhub.state.Stato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hackathon {

    private final String nome;
    private final String regolamento;
    private final LocalDate scadenzaIscrizioni;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final String luogo;
    private final long montepremi;
    private final int dimensioneMaxTeam;
    private Stato stato;
    private final List<Incarico> staff;
    private Team vincitore;

    public Hackathon(String nome,
                     LocalDate dataInizio,
                     LocalDate dataFine,
                     String luogo,
                     String regolamento,
                     LocalDate scadenzaIscrizioni,
                     int dimensioneMaxTeam,
                     long montepremi) {
        this.nome = nome;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.regolamento = regolamento;
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        this.dimensioneMaxTeam = dimensioneMaxTeam;
        this.montepremi = montepremi;
        this.stato = new InIscrizione();
        this.staff = new ArrayList<>();
    }

    public void assegnaStaff(Utente organizzatore, Utente giudice, List<Utente> mentori) {
        staff.add(new Incarico(organizzatore, this, RuoloStaff.Organizzatore));
        staff.add(new Incarico(giudice, this, RuoloStaff.Giudice));
        for (Utente m : mentori) {
            staff.add(new Incarico(m, this, RuoloStaff.Mentore));
        }
    }

    public boolean iscrivibile(Team team) {
        return stato.iscrizioneConsentita() && verificaDimensione(team);
    }

    private boolean verificaDimensione(Team team) {
        return team.numeroMembri() <= dimensioneMaxTeam;
    }

    public boolean iscrizioneConsentita() {
        return stato.iscrizioneConsentita();
    }

    public boolean sottomissioneConsentita() {
        return stato.sottomissioneConsentita();
    }

    public boolean valutazioneConsentita() {
        return stato.valutazioneConsentita();
    }

    // Transizioni del ciclo di vita (UC06/UC07/UC08): l'Hackathon (Context del
    // pattern State) verifica la guardia temporale e delega allo stato corrente,
    // che crea e restituisce lo stato successivo (null = transizione non consentita).
    public boolean avvia() {
        if (!scadenzaIscrizioniRaggiunta()) {
            return false;
        }
        Stato successivo = stato.avvia();
        if (successivo == null) {
            return false;
        }
        this.stato = successivo;
        return true;
    }

    public boolean iniziaFaseValutazione() {
        if (!dataFineRaggiunta()) {
            return false;
        }
        Stato successivo = stato.iniziaValutazione();
        if (successivo == null) {
            return false;
        }
        this.stato = successivo;
        return true;
    }

    public boolean proclamaVincitore(Team teamVincitore) {
        Stato successivo = stato.concludi();
        if (successivo == null) {
            return false;
        }
        this.vincitore = teamVincitore;
        this.stato = successivo;
        return true;
    }

    private boolean scadenzaIscrizioniRaggiunta() {
        return !LocalDate.now().isBefore(scadenzaIscrizioni);
    }

    private boolean dataFineRaggiunta() {
        return !LocalDate.now().isBefore(dataFine);
    }

    public Iscrizione creaIscrizione(Team team) {
        return new Iscrizione(team, this, LocalDate.now());
    }

    public String getNome() {
        return nome;
    }

    public String getRegolamento() {
        return regolamento;
    }

    public LocalDate getScadenzaIscrizioni() {
        return scadenzaIscrizioni;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public String getLuogo() {
        return luogo;
    }

    public long getMontepremi() {
        return montepremi;
    }

    public int getDimensioneMaxTeam() {
        return dimensioneMaxTeam;
    }

    public Stato getStato() {
        return stato;
    }

    public Team getVincitore() {
        return vincitore;
    }

    public List<Incarico> getStaff() {
        return Collections.unmodifiableList(staff);
    }
}
