package com.ehei.auth.service;

import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.Staff.StaffRegistrationRequest;
import com.ehei.auth.DTOS.Staff.StaffResponseDto;
import com.ehei.auth.DTOS.Staff.StaffUpdateRequestAuth;
import com.ehei.auth.DTOS.login.LoginRequest;
import com.ehei.auth.DTOS.login.LoginResponse;
import com.ehei.auth.DTOS.login.RefreshRequest;
import com.ehei.auth.DTOS.medecin.MedecinRegistrationRequest;
import com.ehei.auth.DTOS.medecin.MedecinResponseDto;
import com.ehei.auth.DTOS.medecin.MedecinUpdateRequestAuth;
import com.ehei.auth.DTOS.patient.PatientAccountResult;
import com.ehei.auth.DTOS.patient.PatientRegistrationRequest;
import com.ehei.auth.DTOS.patient.PatientResponse;
import com.ehei.auth.DTOS.patient.PatientUpdateRequestAuth;
import com.ehei.auth.feign.MedecinFeignClient;
import com.ehei.auth.feign.PatientFeignClient;
import com.ehei.auth.feign.StaffFeignClient;
import com.ehei.auth.mapper.MedecinMapper;
import com.ehei.auth.mapper.PatientMapper;
import com.ehei.auth.mapper.StaffMapper;
import com.ehei.auth.model.KeycloakMappable;
import com.ehei.auth.model.PatientKeycloakMapping;
import com.ehei.auth.model.StaffKeycloakMapping;
import com.ehei.auth.repository.PatientKeycloakMappingRepository;
import com.ehei.auth.repository.StaffKeycloakMappingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final StaffFeignClient staffClient;
    private final MedecinFeignClient medecinClient;
    private final KeycloakAdminService keycloakService;

    private final StaffMapper staffMapper;
    private final MedecinMapper medecinMapper;

    private final StaffKeycloakMappingRepository mappingRepository;

    private final PatientFeignClient patientClient;
    private final PatientMapper patientMapper;
    private final PatientKeycloakMappingRepository patientMappingRepository;

    // ==================== STAFF OPERATIONS ====================

    /**
     * Crée un compte Staff + compte Keycloak
     */
    @Transactional
    public StaffAccountResult createStaffWithKeycloakAccount(
            StaffRegistrationRequest staffRegistrationRequest
    ) {
        // 1. Créer le staff (si erreur, FeignException remonte directement)
        StaffResponseDto staff = staffClient.createStaff(
                staffMapper.toStaffCreateRequestDto(staffRegistrationRequest)
        ).getData();

        log.info("Staff créé avec succès - ID: {}, Email: {}", staff.getId(), staff.getEmail());

        // 2. Créer le compte Keycloak avec rollback en cas d'erreur
        try {
            String keycloakId = createKeycloakAccountAndMapping(
                    staff.getId(),
                    staff.getEmail(),
                    staff.getPrenom(),
                    staff.getNom(),
                    staffRegistrationRequest.getPassword(),
                    staff.getRole().toString()
            );

            return StaffAccountResult.success(staff, keycloakId);

        } catch (Exception e) {
            rollbackStaffCreation(staff.getId());
            throw new RuntimeException("Erreur création compte Keycloak: " + e.getMessage());
        }
    }

    /**
     * Met à jour un Staff + compte Keycloak
     */
    @Transactional
    public StaffAccountResult updateStaffWithKeycloakAccount(
            Long staffId,
            StaffUpdateRequestAuth updateRequest
    ) {
        // 1. Récupérer le mapping
        StaffKeycloakMapping mapping = mappingRepository.findByStaffId(staffId)
                .orElseThrow(() -> new RuntimeException("Aucun compte Keycloak trouvé pour ce staff"));

        log.info("Mise à jour du staff {} et compte Keycloak {}", staffId, mapping.getKeycloakUserId());

        // 2. Mettre à jour le staff
        StaffResponseDto staff = staffClient.updateStaff(
                String.valueOf(staffId),
                staffMapper.toStaffUpdateRequestDto(updateRequest)
        ).getData();

        log.info("Staff mis à jour - ID: {}, Email: {}, Rôle: {}",
                staff.getId(), staff.getEmail(), staff.getRole());

        // 3. Synchroniser avec Keycloak
        updateKeycloakUser(mapping, staff.getEmail(), staff.getPrenom(), staff.getNom(),
                staff.getRole().toString(), updateRequest.getPassword(), staffId);

        return StaffAccountResult.success(staff, mapping.getKeycloakUserId());
    }

    /**
     * Supprime un Staff + compte Keycloak
     */
    @Transactional
    public void deleteStaffWithKeycloakAccount(Long staffId) {
        // 1. Récupérer le mapping
        StaffKeycloakMapping mapping = mappingRepository.findByStaffId(staffId)
                .orElseThrow(() -> new RuntimeException("Aucun compte Keycloak trouvé pour ce staff"));

        log.info("Suppression du staff {} et compte Keycloak {}", staffId, mapping.getKeycloakUserId());

        // 2. Supprimer Keycloak
        deleteKeycloakUserSafely(mapping.getKeycloakUserId());

        // 3. Supprimer le mapping
        mappingRepository.deleteByStaffId(staffId);
        log.info("Mapping supprimé pour staff {}", staffId);

        // 4. Supprimer le staff
        staffClient.deleteStaff(staffId);
        log.info("Staff {} supprimé", staffId);
    }

    // ==================== MEDECIN OPERATIONS ====================

    /**
     * Crée un compte Medecin + compte Keycloak
     */
    @Transactional
    public StaffAccountResult createMedecinWithKeycloakAccount(
            @Valid MedecinRegistrationRequest medecinRegistrationRequest
    ) {
        // 1. Créer le médecin (qui crée aussi le staff en cascade)
        MedecinResponseDto medecin = medecinClient.createMedecin(
                medecinMapper.toMedecinCreateRequestDto(medecinRegistrationRequest)
        ).getData();

        log.info("Médecin créé avec succès - ID: {}, Staff ID: {}, Email: {}",
                medecin.getId(), medecin.getStaffId(), medecin.getEmail());

        // 2. Créer le compte Keycloak
        try {
            String keycloakId = createKeycloakAccountAndMapping(
                    medecin.getStaffId(),
                    medecin.getEmail(),
                    medecin.getPrenom(),
                    medecin.getNom(),
                    medecinRegistrationRequest.getPassword(),
                    medecin.getRole().toString()
            );

            // 3. Créer StaffResponseDto pour le retour
            StaffResponseDto staffDto = buildStaffDtoFromMedecin(medecin);

            return StaffAccountResult.success(staffDto, keycloakId);

        } catch (Exception e) {
            rollbackMedecinCreation(medecin.getId());
            throw new RuntimeException("Erreur création compte Keycloak: " + e.getMessage());
        }
    }

    /**
     * Met à jour un Medecin + compte Keycloak
     * Utilise le medecinId car la logique de mise à jour est différente de Staff
     */
    @Transactional
    public StaffAccountResult updateMedecinWithKeycloakAccount(
            Long medecinId,
            MedecinUpdateRequestAuth updateRequest
    ) {
        // 1. Mettre à jour le médecin (qui met à jour aussi le Staff + spécialité)
        MedecinResponseDto medecin = medecinClient.updateMedecinById(
                String.valueOf(medecinId),
                medecinMapper.toMedecinUpdateRequestDto(updateRequest)
        ).getData();

        log.info("Médecin mis à jour - ID: {}, Staff ID: {}, Email: {}",
                medecin.getId(), medecin.getStaffId(), medecin.getEmail());

        // 2. Récupérer le mapping via staffId
        StaffKeycloakMapping mapping = mappingRepository.findByStaffId(medecin.getStaffId())
                .orElseThrow(() -> new RuntimeException("Aucun compte Keycloak trouvé pour ce médecin"));

        // 3. Synchroniser avec Keycloak
        updateKeycloakUser(mapping, medecin.getEmail(), medecin.getPrenom(), medecin.getNom(),
                medecin.getRole().toString(), updateRequest.getPassword(), medecinId);

        // 4. Créer StaffResponseDto pour le retour
        StaffResponseDto staffDto = buildStaffDtoFromMedecin(medecin);

        return StaffAccountResult.success(staffDto, mapping.getKeycloakUserId());
    }

    /**
     * Supprime un Medecin + compte Keycloak
     * Utilise le medecinId car la suppression en cascade est gérée côté Staff
     */
    @Transactional
    public void deleteMedecinWithKeycloakAccount(Long medecinId) {
        // 1. Récupérer le médecin pour obtenir le staffId
        MedecinResponseDto medecin = medecinClient.getMedecinById(String.valueOf(medecinId)).getData();
        Long staffId = medecin.getStaffId();

        // 2. Récupérer le mapping
        StaffKeycloakMapping mapping = mappingRepository.findByStaffId(staffId)
                .orElseThrow(() -> new RuntimeException("Aucun compte Keycloak trouvé pour ce médecin"));

        log.info("Suppression du médecin {} (staff {}) et compte Keycloak {}",
                medecinId, staffId, mapping.getKeycloakUserId());

        // 3. Supprimer Keycloak
        deleteKeycloakUserSafely(mapping.getKeycloakUserId());

        // 4. Supprimer le mapping
        mappingRepository.deleteByStaffId(staffId);
        log.info("Mapping supprimé pour médecin {}", medecinId);

        // 5. Supprimer le médecin (qui supprime aussi le Staff en cascade)
        medecinClient.deleteMedecinById(String.valueOf(medecinId));
        log.info("Médecin {} supprimé (cascade: Staff {} supprimé aussi)", medecinId, staffId);
    }

    // ==================== PATIENT OPERATIONS ====================

    /**
     * Crée un compte Patient + compte Keycloak
     */
    @Transactional
    public PatientAccountResult createPatientWithKeycloakAccount(PatientRegistrationRequest request) {
        // 1. Créer le patient
        PatientResponse patient = patientClient.create(
                patientMapper.toPatientCreateRequestDto(request)
        ).getData();

        log.info("Patient créé avec succès - ID: {}, Email: {}", patient.getId(), patient.getEmail());

        // 2. Créer le compte Keycloak avec rollback en cas d'erreur
        try {
            String keycloakId = keycloakService.createUser(
                    patient.getEmail(),
                    patient.getPrenom(),
                    patient.getNom(),
                    request.getPassword(),
                    "PATIENT"
            );

            log.info("Compte Keycloak créé - ID: {}, Email: {}, Rôle: PATIENT", keycloakId, patient.getEmail());

            // 3. Sauvegarder le mapping
            PatientKeycloakMapping mapping = new PatientKeycloakMapping();
            mapping.setPatientId(patient.getId());
            mapping.setKeycloakUserId(keycloakId);
            mapping.setEmail(patient.getEmail());
            mapping.setCreatedAt(LocalDateTime.now());
            patientMappingRepository.save(mapping);

            log.info("Mapping créé: Patient {} ↔ Keycloak {}", patient.getId(), keycloakId);

            return PatientAccountResult.success(patient, keycloakId);

        } catch (Exception e) {
            rollbackPatientCreation(patient.getId());
            throw new RuntimeException("Erreur création compte Keycloak: " + e.getMessage());
        }
    }

    /**
     * Met à jour un Patient + compte Keycloak
     */
    @Transactional
    public PatientAccountResult updatePatientWithKeycloakAccount(Long patientId, PatientUpdateRequestAuth request) {
        // 1. Récupérer le mapping
        PatientKeycloakMapping mapping = patientMappingRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Aucun compte Keycloak trouvé pour ce patient"));

        log.info("Mise à jour du patient {} et compte Keycloak {}", patientId, mapping.getKeycloakUserId());

        // 2. Mettre à jour le patient
        PatientResponse patient = patientClient.updatePatient(
                String.valueOf(patientId),
                patientMapper.toPatientUpdateRequestDto(request)
        ).getData();

        log.info("Patient mis à jour - ID: {}, Email: {}", patient.getId(), patient.getEmail());

        // 3. Synchroniser avec Keycloak
        updateKeycloakUser(
                mapping,
                patient.getEmail(),
                patient.getPrenom(),
                patient.getNom(),
                "PATIENT",
                request.getPassword(),
                patientId
        );

        return PatientAccountResult.success(patient, mapping.getKeycloakUserId());
    }

    /**
     * Supprime un Patient + compte Keycloak
     */
    @Transactional
    public void deletePatientWithKeycloakAccount(Long patientId) {
        // 1. Récupérer le mapping
        PatientKeycloakMapping mapping = patientMappingRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Aucun compte Keycloak trouvé pour ce patient"));

        log.info("Suppression du patient {} et compte Keycloak {}", patientId, mapping.getKeycloakUserId());

        // 2. Supprimer Keycloak
        deleteKeycloakUserSafely(mapping.getKeycloakUserId());

        // 3. Supprimer le mapping
        patientMappingRepository.deleteByPatientId(patientId);
        log.info("Mapping supprimé pour patient {}", patientId);

        // 4. Supprimer le patient
        patientClient.deletePatient(String.valueOf(patientId));
        log.info("Patient {} supprimé", patientId);
    }

// ==================== ROLLBACK PATIENT ====================

    private void rollbackPatientCreation(Long patientId) {
        log.error("Erreur Keycloak, rollback du patient {}", patientId);
        try {
            patientClient.deletePatient(String.valueOf(patientId));
            log.info("Patient {} supprimé avec succès (rollback)", patientId);
        } catch (Exception rollbackEx) {
            log.error("ERREUR CRITIQUE: Impossible de supprimer le patient {} lors du rollback", patientId);
        }
    }

    // ==================== MÉTHODES PRIVÉES COMMUNES ====================

    /**
     * Crée un compte Keycloak et le mapping associé
     */
    private String createKeycloakAccountAndMapping(
            Long staffId,
            String email,
            String firstName,
            String lastName,
            String password,
            String role
    ) {
        // 1. Créer le compte Keycloak
        String keycloakId = keycloakService.createUser(
                email,
                firstName,
                lastName,
                password,
                role
        );

        log.info("Compte Keycloak créé - ID: {}, Email: {}, Rôle: {}", keycloakId, email, role);

        // 2. Sauvegarder le mapping
        StaffKeycloakMapping mapping = new StaffKeycloakMapping();
        mapping.setStaffId(staffId);
        mapping.setKeycloakUserId(keycloakId);
        mapping.setEmail(email);
        mapping.setCreatedAt(LocalDateTime.now());
        mappingRepository.save(mapping);

        log.info("Mapping créé: Staff {} ↔ Keycloak {}", staffId, keycloakId);

        return keycloakId;
    }

    /**
     * Met à jour un utilisateur Keycloak et son mapping
     * Méthode commune pour Staff et Medecin et Patient grace a l'interface
     */
    private void updateKeycloakUser(
            KeycloakMappable mapping,
            String email,
            String prenom,
            String nom,
            String role,
            String newPassword,
            Long entityId
    ) {
        try {
            // 1. Mettre à jour les infos + rôle
            keycloakService.updateUser(
                    mapping.getKeycloakUserId(),
                    email,
                    prenom,
                    nom,
                    role
            );

            // 2. Mettre à jour le mot de passe si fourni
            if (newPassword != null && !newPassword.isEmpty()) {
                keycloakService.updatePassword(
                        mapping.getKeycloakUserId(),
                        newPassword
                );
                log.info("Mot de passe mis à jour pour entité {}", entityId);
            }

            // 3. Mettre à jour le mapping si l'email a changé
            if (!mapping.getEmail().equals(email)) {
                mapping.setEmail(email);
                mapping.setUpdatedAt(LocalDateTime.now());

                //TODO: ajouter save() dans le modele
                // Sauvegarder selon le type
                if (mapping instanceof StaffKeycloakMapping) {
                    mappingRepository.save((StaffKeycloakMapping) mapping);
                } else if (mapping instanceof PatientKeycloakMapping) {
                    patientMappingRepository.save((PatientKeycloakMapping) mapping);
                }

                log.info("Mapping mis à jour pour entité {}", entityId);
            }

        } catch (Exception e) {
            log.error("Erreur mise à jour Keycloak pour entité {}: {}", entityId, e.getMessage());
            throw new RuntimeException("Erreur mise à jour compte Keycloak: " + e.getMessage());
        }
    }

    /**
     * Supprime un utilisateur Keycloak de manière sécurisée
     */
    private void deleteKeycloakUserSafely(String keycloakUserId) {
        try {
            keycloakService.deleteUser(keycloakUserId);
            log.info("Compte Keycloak {} supprimé", keycloakUserId);
        } catch (Exception e) {
            log.error("Erreur suppression Keycloak: {}", e.getMessage());
            // On continue quand même pour éviter les données orphelines
        }
    }

    /**
     * Construit un StaffResponseDto depuis un MedecinResponseDto
     */
    private StaffResponseDto buildStaffDtoFromMedecin(MedecinResponseDto medecin) {
        return StaffResponseDto.builder()
                .id(medecin.getStaffId())
                .nom(medecin.getNom())
                .prenom(medecin.getPrenom())
                .email(medecin.getEmail())
                .tel(medecin.getTel())
                .dateNaissance(medecin.getDateNaissance())
                .role(medecin.getRole())
                .build();
    }

    // ==================== ROLLBACK METHODS ====================

    /**
     * Rollback: supprime un staff en cas d'erreur lors de la création Keycloak
     */
    private void rollbackStaffCreation(Long staffId) {
        log.error("Erreur Keycloak, rollback du staff {}", staffId);
        try {
            staffClient.deleteStaff(staffId);
            log.info("Staff {} supprimé avec succès (rollback)", staffId);
        } catch (Exception rollbackEx) {
            log.error("ERREUR CRITIQUE: Impossible de supprimer le staff {} lors du rollback", staffId);
        }
    }

    /**
     * Rollback: supprime un médecin en cas d'erreur lors de la création Keycloak
     */
    private void rollbackMedecinCreation(Long medecinId) {
        log.error("Erreur Keycloak, rollback du médecin {}", medecinId);
        try {
            medecinClient.deleteMedecinById(String.valueOf(medecinId));
            log.info("Médecin {} supprimé avec succès (rollback)", medecinId);
        } catch (Exception rollbackEx) {
            log.error("ERREUR CRITIQUE: Impossible de supprimer le médecin {} lors du rollback", medecinId);
        }
    }

    // ==================== LOGIN METHODS ====================

    public LoginResponse login(LoginRequest request) {
        return keycloakService.login(request.getUsername(), request.getPassword());
    }

    public LoginResponse refresh(RefreshRequest request) {
        return keycloakService.refresh(request.getRefreshToken());
    }
}