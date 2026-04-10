package com.ehei.auth.service;

import com.ehei.auth.DTOS.login.LoginResponse;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${keycloak.client-id}")
    private String clientId;

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

    public String createUser(String email, String firstName, String lastName, String password, String role, Long internalId) {
        // 1. Créer l'user SANS attribut
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

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // 2. Récupérer l'user créé et lui ajouter l'attribut
        UserRepresentation createdUser = keycloak.realm(realm).users().get(userId).toRepresentation();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("internalId", List.of(internalId.toString()));
        createdUser.setAttributes(attributes);
        keycloak.realm(realm).users().get(userId).update(createdUser);

        log.info("Attribut internalId={} ajouté à l'utilisateur {}", internalId, userId);

        // 3. Password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        keycloak.realm(realm).users().get(userId).resetPassword(credential);

        // 4. Rôle
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

    public LoginResponse login(String username, String password) {
        try {
            Keycloak userKeycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .username(username)
                    .password(password)
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();

            AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();

            return new LoginResponse(
                    tokenResponse.getToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getExpiresIn()
            );

        } catch (Exception e) {
            log.error("Erreur login pour {}: {}", username, e.getMessage());
            throw new RuntimeException("Identifiants incorrects");
        }
    }

    public LoginResponse refresh(String refreshToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", OAuth2Constants.REFRESH_TOKEN);
            body.add("client_id", clientId);
            body.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    entity,
                    AccessTokenResponse.class
            );

            AccessTokenResponse tokenResponse = response.getBody();

            if (tokenResponse == null) {
                throw new RuntimeException("Refresh token invalide ou expiré");
            }

            return new LoginResponse(
                    tokenResponse.getToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getExpiresIn()
            );

        } catch (Exception e) {
            log.error("Erreur refresh token: {}", e.getMessage());
            throw new RuntimeException("Refresh token invalide ou expiré");
        }
    }
}