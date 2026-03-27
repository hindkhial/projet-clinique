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
import java.util.List;

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

        // Assigner le rôle
        assignRoleToUser(userId, role);

        log.info("Utilisateur créé avec succès - ID: {}, Email: {}, Rôle: {}", userId, email, role);

        return userId;
    }

    private void assignRoleToUser(String userId, String roleName) {
        try {
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

            log.info("Rôle {} assigné à l'utilisateur {}", roleName, userId);

        } catch (Exception e) {
            log.error("Erreur lors de l'assignation du rôle {} à l'utilisateur {}: {}",
                    roleName, userId, e.getMessage());
            throw new RuntimeException("Erreur assignation rôle: " + e.getMessage(), e);
        }
    }

    public void deleteUser(String userId) {
        try {
            keycloak.realm(realm).users().get(userId).remove();
            log.info("Utilisateur {} supprimé avec succès", userId);
        } catch (Exception e) {
            log.error("Erreur suppression utilisateur {}: {}", userId, e.getMessage());
            throw new RuntimeException("Erreur suppression utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour un utilisateur Keycloak (informations personnelles + rôle)
     *
     * @param keycloakUserId ID Keycloak de l'utilisateur
     * @param email Nouvel email
     * @param prenom Nouveau prénom
     * @param nom Nouveau nom
     * @param newRole Nouveau rôle (obligatoire pour garantir la cohérence)
     */
    public void updateUser(String keycloakUserId, String email, String prenom, String nom, String newRole) {
        try {
            log.info("Mise à jour utilisateur Keycloak: {}", keycloakUserId);
            log.debug("Nouvelles valeurs - Email: {}, Prénom: {}, Nom: {}, Rôle: {}",
                    email, prenom, nom, newRole);

            // 1. Récupérer l'utilisateur existant
            UserRepresentation user = keycloak.realm(realm)
                    .users()
                    .get(keycloakUserId)
                    .toRepresentation();

            log.debug("Utilisateur actuel - Email: {}, Username: {}", user.getEmail(), user.getUsername());

            // 2. Mettre à jour les informations personnelles
            user.setEmail(email);
            user.setFirstName(prenom);
            user.setLastName(nom);

            // 3. Sauvegarder les modifications
            keycloak.realm(realm)
                    .users()
                    .get(keycloakUserId)
                    .update(user);

            log.info("Informations personnelles mises à jour pour l'utilisateur {}", keycloakUserId);

            // 4. Mettre à jour le rôle
            updateUserRole(keycloakUserId, newRole);

            log.info("Utilisateur Keycloak {} complètement mis à jour", keycloakUserId);

        } catch (jakarta.ws.rs.BadRequestException e) {
            log.error("Erreur 400 Keycloak - Détails: {}", e.getResponse().readEntity(String.class));
            throw new RuntimeException("Erreur mise à jour Keycloak: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erreur mise à jour utilisateur Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur mise à jour Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour le rôle d'un utilisateur
     * Supprime tous les anciens rôles realm-level et assigne le nouveau
     *
     * @param keycloakUserId ID Keycloak de l'utilisateur
     * @param newRole Nouveau rôle à assigner
     */
    private void updateUserRole(String keycloakUserId, String newRole) {
        try {
            // 1. Récupérer les rôles actuels de l'utilisateur
            List<RoleRepresentation> currentRoles = keycloak.realm(realm)
                    .users()
                    .get(keycloakUserId)
                    .roles()
                    .realmLevel()
                    .listAll();

            log.debug("Rôles actuels de l'utilisateur {}: {}", keycloakUserId,
                    currentRoles.stream().map(RoleRepresentation::getName).toList());

            // 2. Supprimer tous les anciens rôles
            if (!currentRoles.isEmpty()) {
                keycloak.realm(realm)
                        .users()
                        .get(keycloakUserId)
                        .roles()
                        .realmLevel()
                        .remove(currentRoles);

                log.info("Anciens rôles supprimés pour l'utilisateur {}", keycloakUserId);
            }

            // 3. Assigner le nouveau rôle
            assignRoleToUser(keycloakUserId, newRole);

            log.info("Rôle mis à jour: {} → {} pour l'utilisateur {}",
                    currentRoles.isEmpty() ? "aucun" : currentRoles.get(0).getName(),
                    newRole,
                    keycloakUserId);

        } catch (Exception e) {
            log.error("Erreur mise à jour rôle pour utilisateur {}: {}", keycloakUserId, e.getMessage());
            throw new RuntimeException("Erreur mise à jour rôle: " + e.getMessage(), e);
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