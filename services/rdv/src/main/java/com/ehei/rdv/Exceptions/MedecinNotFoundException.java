package com.ehei.rdv.Exceptions;

public class MedecinNotFoundException extends RuntimeException {

    public MedecinNotFoundException(Long id) {
        super("Médecin avec id " + id + " introuvable");
    }
}