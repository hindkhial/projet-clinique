package com.ehei.patient.exceptions;

public class EmailAlreadyExistException extends RuntimeException
{
    public EmailAlreadyExistException(String email)
    {
        super("Email existe deja: " + email);
    }
}
