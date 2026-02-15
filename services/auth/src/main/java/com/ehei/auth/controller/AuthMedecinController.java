package com.ehei.auth.controller;

import com.ehei.auth.DTOS.ApiResponse;
import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.medecin.MedecinRegistrationRequest;
import com.ehei.auth.DTOS.medecin.MedecinUpdateRequestAuth;
import com.ehei.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/medecin")
@RequiredArgsConstructor
public class AuthMedecinController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<StaffAccountResult>> registerMedecin(
            @Valid @RequestBody MedecinRegistrationRequest request
    ) {
        StaffAccountResult result = authService.createMedecinWithKeycloakAccount(request);

        return ResponseEntity.ok(
                ApiResponse.success(result, "Médecin et compte créés avec succès")
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<StaffAccountResult>> updateMedecin(
            @PathVariable Long id,
            @Valid @RequestBody MedecinUpdateRequestAuth request
    ) {
        StaffAccountResult result = authService.updateMedecinWithKeycloakAccount(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(result, "Médecin et compte mis à jour avec succès")
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMedecin(@PathVariable Long id) {
        authService.deleteMedecinWithKeycloakAccount(id);

        return ResponseEntity.ok(
                ApiResponse.success("Médecin et compte supprimés avec succès")
        );
    }
}