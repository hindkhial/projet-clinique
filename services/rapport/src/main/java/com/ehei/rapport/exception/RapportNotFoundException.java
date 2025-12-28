package com.ehei.rapport.exception;

public class RapportNotFoundException extends RuntimeException {

    public RapportNotFoundException(Long id) {
        super("Rapport non trouve avec l'ID: " + id);
    }

    public RapportNotFoundException(String message) {
        super(message);
    }

}
