package ru.moskalkov.vdcom.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistsException extends ResponseStatusException {

    public UserAlreadyExistsException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
