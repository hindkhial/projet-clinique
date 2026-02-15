package com.ehei.auth.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class KeycloakAdminService {

    @Value("${keycloak.url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm("master")
                .username(adminUsername)
                .password(adminPassword)
                .clientId("admin-cli")
                .build();
    }

    public String createUser(String email, String firstName, String lastName, String password, String role) {
        // Créer user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);


        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Erreur Keycloak: " + response.getStatus());
        }

        // Récupérer l'ID
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // Définir le mot de passe
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        keycloak.realm(realm).users().get(userId).resetPassword(credential);

        assignRoleToUser(userId, role);

        return userId;
    }

    private void assignRoleToUser(String userId, String roleName) {
        // Récupérer le rôle depuis le realm
        RoleRepresentation role = keycloak.realm(realm)
                .roles()
                .get(roleName)
                .toRepresentation();

        // Assigner le rôle à l'utilisateur
        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(Arrays.asList(role));
    }

    public void deleteUser(String userId) {
        keycloak.realm(realm).users().get(userId).remove();
    }
}
