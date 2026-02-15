package com.ehei.auth.controller;

import com.ehei.auth.DTOS.ApiResponse;
import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.Staff.StaffRegistrationRequest;
import com.ehei.auth.DTOS.Staff.StaffUpdateRequestAuth;
import com.ehei.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/staff")
@RequiredArgsConstructor
public class AuthStaffController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<StaffAccountResult>> registerStaff(
            @Valid @RequestBody StaffRegistrationRequest request
    ) {
        StaffAccountResult result = authService.createStaffWithKeycloakAccount(request);

        return ResponseEntity.ok(
                ApiResponse.success(result, "Staff et compte créés avec succès")
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<StaffAccountResult>> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody StaffUpdateRequestAuth request
    ) {
        StaffAccountResult result = authService.updateStaffWithKeycloakAccount(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(result, "Staff et compte mis à jour avec succès")
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteStaff(@PathVariable Long id) {
        authService.deleteStaffWithKeycloakAccount(id);

        return ResponseEntity.ok(
                ApiResponse.success("Staff et compte supprimés avec succès")
        );
    }
}