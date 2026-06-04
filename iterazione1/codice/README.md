# HackHub 🚀

HackHub è un sistema gestionale per hackathon che permette di coordinare team, staff (organizzatori, giudici, mentori) e sottomissioni. Il progetto segue l'intero ciclo di vita di un evento: dall'iscrizione alla premiazione finale.

## 📌 Funzionalità Principali
- **Gestione Ruoli:** Accessi differenziati per Organizzatori, Mentori, Giudici e Team.
- **Ciclo di Vita:** Stati dinamici (In iscrizione, In corso, In valutazione, Concluso).
- **Integrazioni Esterne:** Simulazione di sistemi Calendar e Payment.
- **Segnalazioni e Supporto:** Sistema di richiesta aiuto ai mentori e segnalazioni violazioni.

## 🛠️ Tech Stack
- **Linguaggio:** Java
- **Framework:** Spring Boot (migrazione prevista)
- **Architettura:** API REST / CLI
- **Pattern utilizzati:** [Inserisci qui i 2+ pattern scelti, es. Factory, Strategy, Observer]

## 🏗️ Struttura del Progetto
Il progetto è diviso in due fasi:
1. **v1-core:** Implementazione logica in Java puro.
2. **v2-springboot:** Porting dell'applicazione su Spring Boot.

## 👥 Attori del Sistema
- **Organizzatore:** Crea eventi e proclama il vincitore.
- **Mentore:** Supporta i team e segnala violazioni.
- **Giudice:** Valuta le sottomissioni (voto 0-10).
- **Membro Team:** Gestisce iscrizioni e sottomissioni.
