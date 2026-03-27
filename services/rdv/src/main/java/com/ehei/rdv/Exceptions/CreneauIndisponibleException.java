package com.ehei.rdv.Exceptions;

public class CreneauIndisponibleException extends RuntimeException {

    public CreneauIndisponibleException() {
        super("Ce créneau est déjà réservé pour ce médecin");
    }
}