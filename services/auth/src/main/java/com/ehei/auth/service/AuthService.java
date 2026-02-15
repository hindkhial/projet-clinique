package com.ehei.auth.service;

import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.Staff.StaffRegistrationRequest;
import com.ehei.auth.DTOS.Staff.StaffResponseDto;
import com.ehei.auth.DTOS.Staff.StaffUpdateRequestAuth;
import com.ehei.auth.feign.StaffFeignClient;
import com.ehei.auth.mapper.StaffMapper;
import com.ehei.auth.model.StaffKeycloakMapping;
import com.ehei.auth.repository.StaffKeycloakMappingRepository;
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
    private final KeycloakAdminService keycloakService;
    private final StaffMapper staffMapper;
    private final StaffKeycloakMappingRepository mappingRepository;

    @Transactional
    public StaffAccountResult createStaffWithKeycloakAccount(
            StaffRegistrationRequest staffRegistrationRequest
    ) {
        // 1. Créer le staff (si erreur, FeignException remonte directement)
        StaffResponseDto staff = staffClient.createStaff(
                staffMapper.toStaffCreateRequestDto(staffRegistrationRequest)
        ).getData();

        // 2. Créer Keycloak avec rollback en cas d'erreur
        try {
            String keycloakId = keycloakService.createUser(
                    staff.getEmail(),
                    staff.getPrenom(),
                    staff.getNom(),
                    staffRegistrationRequest.getPassword(),
                    staff.getRole().toString()
            );

            // 3. Sauvegarder le mapping
            StaffKeycloakMapping mapping = new StaffKeycloakMapping();
            mapping.setStaffId(staff.getId());
            mapping.setKeycloakUserId(keycloakId);
            mapping.setEmail(staff.getEmail());
            mapping.setCreatedAt(LocalDateTime.now());
            mappingRepository.save(mapping);

            log.info("Mapping créé: Staff {} ↔ Keycloak {}", staff.getId(), keycloakId);

            return StaffAccountResult.success(staff, keycloakId);

        } catch (Exception e) {
            // Rollback: supprimer le staff créé
            log.error("Erreur Keycloak, rollback du staff {}", staff.getId());

            try {
                staffClient.deleteStaff(staff.getId());
                log.info("Staff {} supprimé avec succès (rollback)", staff.getId());
            } catch (Exception rollbackEx) {
                log.error("ERREUR CRITIQUE: Impossible de supprimer le staff {}", staff.getId());
            }

            throw new RuntimeException("Erreur création compte Keycloak: " + e.getMessage());
        }
    }

    @Transactional
    public StaffAccountResult updateStaffWithKeycloakAccount(
            Long staffId,
            StaffUpdateRequestAuth updateRequest
    ) {
        // 1. Récupérer le mapping
        StaffKeycloakMapping mapping = mappingRepository.findByStaffId(staffId)
                .orElseThrow(() -> new RuntimeException("Aucun compte Keycloak trouvé pour ce staff"));

        // 2. Mettre à jour le staff
        StaffResponseDto staff = staffClient.updateStaff(
                String.valueOf(staffId),
                staffMapper.toStaffUpdateRequestDto(updateRequest)
        ).getData();

        // 3. Mettre à jour Keycloak
        try {
            keycloakService.updateUser(
                    mapping.getKeycloakUserId(),
                    staff.getEmail(),
                    staff.getPrenom(),
                    staff.getNom()
            );

            // Si un nouveau mot de passe est fourni
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                keycloakService.updatePassword(
                        mapping.getKeycloakUserId(),
                        updateRequest.getPassword()
                );
                log.info("Mot de passe mis à jour pour staff {}", staffId);
            }

            // 4. Mettre à jour le mapping si l'email a changé
            if (!mapping.getEmail().equals(staff.getEmail())) {
                mapping.setEmail(staff.getEmail());
                mapping.setUpdatedAt(LocalDateTime.now());
                mappingRepository.save(mapping);
                log.info("Mapping mis à jour pour staff {}", staffId);
            }

            return StaffAccountResult.success(staff, mapping.getKeycloakUserId());

        } catch (Exception e) {
            log.error("Erreur mise à jour Keycloak pour staff {}: {}", staffId, e.getMessage());
            throw new RuntimeException("Erreur mise à jour compte Keycloak: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteStaffWithKeycloakAccount(Long staffId) {
        // 1. Récupérer le mapping
        StaffKeycloakMapping mapping = mappingRepository.findByStaffId(staffId)
                .orElseThrow(() -> new RuntimeException("Aucun compte Keycloak trouvé pour ce staff"));

        // 2. Supprimer de Keycloak
        try {
            keycloakService.deleteUser(mapping.getKeycloakUserId());
            log.info("Compte Keycloak {} supprimé", mapping.getKeycloakUserId());
        } catch (Exception e) {
            log.error("Erreur suppression Keycloak: {}", e.getMessage());
            // On continue quand même
        }

        // 3. Supprimer le mapping
        mappingRepository.deleteByStaffId(staffId);
        log.info("Mapping supprimé pour staff {}", staffId);

        // 4. Supprimer le staff
        staffClient.deleteStaff(staffId);
        log.info("Staff {} supprimé", staffId);
    }
}