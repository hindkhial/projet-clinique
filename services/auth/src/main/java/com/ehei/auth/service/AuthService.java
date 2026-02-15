package com.ehei.auth.service;

import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.Staff.StaffCreateRequestDto;
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
        StaffResponseDto staff = null;

        try {
            // 1. Créer le staff
            staff = staffClient.createStaff(staffMapper.toStaffCreateRequestDto(staffRegistrationRequest)).getData();

            // 2. Créer Keycloak
            String keycloakId = keycloakService.createUser(
                    staff.getEmail(),
                    staff.getPrenom(),
                    staff.getNom(),
                    staffRegistrationRequest.getPassword(),
                    staff.getRole().toString()
            );

            return StaffAccountResult.success(staff, keycloakId);

        } catch (Exception e) {
            // Si quelque chose échoue, supprimer le staff si créé
            if (staff != null) {
                try {
                    staffClient.deleteStaff(staff.getId());
                    log.info("Delete staff with id {}", staff.getId());
                } catch (Exception ignored) {}
            }
            throw new RuntimeException("Erreur: " + e.getMessage());
        }
    }
}
