package com.ehei.patient.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Arrays;
import java.util.List;

public class SecurityUtils {

    public static List<String> parseRoles(String rolesHeader) {
        return Arrays.asList(rolesHeader.split(","));
    }

    public static void requireRole(String rolesHeader, String role) {
        if (!parseRoles(rolesHeader).contains(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Accès refusé : rôle " + role + " requis");
        }
    }
}