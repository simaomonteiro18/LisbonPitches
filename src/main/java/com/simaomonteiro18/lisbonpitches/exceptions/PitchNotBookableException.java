package com.simaomonteiro18.lisbonpitches.exceptions;

public class PitchNotBookableException extends RuntimeException {
    public PitchNotBookableException(String message) {
        super(message);
    }
}
