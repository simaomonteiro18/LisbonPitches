package com.simaomonteiro18.pitchbooking.exceptions;

public class UsernameConflictException extends RuntimeException {
    public UsernameConflictException(String message) {
        super(message);
    }
}
