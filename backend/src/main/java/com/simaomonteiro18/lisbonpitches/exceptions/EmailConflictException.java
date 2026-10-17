package com.simaomonteiro18.lisbonpitches.exceptions;

public class EmailConflictException extends RuntimeException {
    public EmailConflictException(String message) {
        super(message);
    }
}
