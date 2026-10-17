package com.simaomonteiro18.lisbonpitches.exceptions;

public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException(String message) {
        super(message);
    }
}
