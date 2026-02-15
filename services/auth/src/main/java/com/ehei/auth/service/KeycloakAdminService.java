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

    public void updateUser(String keycloakUserId, String email, String prenom, String nom) {
        try {
            log.info("Mise à jour utilisateur Keycloak: {}", keycloakUserId);
            log.debug("Nouvelles valeurs - Email: {}, Prénom: {}, Nom: {}", email, prenom, nom);

            // 1. Récupérer l'utilisateur existant
            UserRepresentation user = keycloak.realm(realm)
                    .users()
                    .get(keycloakUserId)
                    .toRepresentation();

            log.debug("Utilisateur actuel - Email: {}, Username: {}", user.getEmail(), user.getUsername());

            // 2. Mettre à jour les informations
            user.setEmail(email);
            user.setFirstName(prenom);
            user.setLastName(nom);

            // 3. Sauvegarder les modifications
            keycloak.realm(realm)
                    .users()
                    .get(keycloakUserId)
                    .update(user);

            log.info("Utilisateur Keycloak {} mis à jour", keycloakUserId);

        } catch (jakarta.ws.rs.BadRequestException e) {
            log.error("Erreur 400 Keycloak - Détails: {}", e.getResponse().readEntity(String.class));
            throw new RuntimeException("Erreur mise à jour Keycloak: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erreur mise à jour utilisateur Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur mise à jour Keycloak: " + e.getMessage(), e);
        }
    }

    public void updatePassword(String keycloakUserId, String newPassword) {
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            keycloak.realm(realm)
                    .users()
                    .get(keycloakUserId)
                    .resetPassword(credential);

            log.info("Mot de passe mis à jour pour utilisateur {}", keycloakUserId);

        } catch (Exception e) {
            log.error("Erreur mise à jour mot de passe: {}", e.getMessage());
            throw new RuntimeException("Erreur mise à jour mot de passe: " + e.getMessage(), e);
        }
    }
}
