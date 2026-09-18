package com.simaomonteiro18.pitchbooking.exceptions;

public class PitchNotBookableException extends RuntimeException {
    public PitchNotBookableException(String message) {
        super(message);
    }
}
