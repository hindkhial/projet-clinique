package com.ehei.staff.exceptions;

public class EmailAlreadyExistException extends RuntimeException
{
    public EmailAlreadyExistException(String email)
    {
        super("Email existe deja: " + email);
    }
}
