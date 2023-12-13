package ru.moskalkov.vdcom.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ReadFromFileException extends ResponseStatusException {

    public ReadFromFileException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
