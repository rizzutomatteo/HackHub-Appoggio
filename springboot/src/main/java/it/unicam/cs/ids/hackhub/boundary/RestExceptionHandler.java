package it.unicam.cs.ids.hackhub.boundary;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Gestore centralizzato delle eccezioni per tutti i controller REST (boundary).
// In stile corso: traduce le eccezioni del control/domain in risposte HTTP con un JSON semplice.
@ControllerAdvice
public class RestExceptionHandler {

    // Errori di validazione / precondizioni violate dal dominio -> 400 Bad Request.
    // I service lanciano IllegalArgumentException quando i dati o lo stato non sono validi.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> gestisciArgomentoNonValido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errore", ex.getMessage()));
    }

    // Qualsiasi altro errore non previsto -> 500 Internal Server Error.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> gestisciGenerico(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("errore", ex.getMessage()));
    }
}
