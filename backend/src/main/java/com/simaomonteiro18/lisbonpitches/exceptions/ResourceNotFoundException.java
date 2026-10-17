package com.simaomonteiro18.lisbonpitches.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class<?> classe, Long id) {
        super(classe.getSimpleName() + " com id " + id + " não encontrado");
    }
}
