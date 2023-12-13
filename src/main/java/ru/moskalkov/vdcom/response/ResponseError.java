package ru.moskalkov.vdcom.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseError {
    private int status;
    private String message;
}
