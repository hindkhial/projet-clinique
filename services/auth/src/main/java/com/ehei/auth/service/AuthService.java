package com.ehei.auth.service;

import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.Staff.StaffRegistrationRequest;
import com.ehei.auth.DTOS.Staff.StaffResponseDto;
import com.ehei.auth.feign.StaffFeignClient;
import com.ehei.auth.mapper.StaffMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final StaffFeignClient staffClient;
    private final KeycloakAdminService keycloakService;
    private final StaffMapper staffMapper;

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

            return StaffAccountResult.success(staff, keycloakId);

        } catch (Exception e) {
            // Rollback: supprimer le staff créé
            log.error("Erreur Keycloak, rollback du staff {}", staff.getId());

            try {
                staffClient.deleteStaff(staff.getId());
                log.info("Staff {} supprimé avec succès", staff.getId());
            } catch (Exception rollbackEx) {
                log.error("ERREUR CRITIQUE: Impossible de supprimer le staff {}", staff.getId());
            }

            throw new RuntimeException("Erreur création compte Keycloak: " + e.getMessage());
        }
    }
}