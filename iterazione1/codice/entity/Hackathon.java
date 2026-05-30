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

    public List<Incarico> getStaff() {
        return Collections.unmodifiableList(staff);
    }
}
