package com.ehei.rdv.Exceptions;

public class PatientNotFoundException extends RuntimeException
{
    public PatientNotFoundException(Long id) {
        super("Patient avec id " + id + " introuvable");
    }
}
