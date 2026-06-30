package it.unicam.cs.ids.hackhub.entity;

// Stato di Segnalazione: enum semplice (NON pattern State).
// Aperta -> Gestita (decisione dell'Organizzatore) oppure Aperta -> Archiviata (nessun provvedimento).
public enum StatoSegnalazione {
    Aperta,
    Gestita,
    Archiviata
}
