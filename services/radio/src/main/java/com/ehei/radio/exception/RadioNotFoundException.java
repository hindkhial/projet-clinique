package com.ehei.radio.exception;

public class RadioNotFoundException extends RuntimeException {

    public RadioNotFoundException(Long id) {
        super("Radio non trouvé avec l'ID: " + id);
    }

    public RadioNotFoundException(String message) {
        super(message);
    }
}

// ============ InvalidRadioException.java ============


