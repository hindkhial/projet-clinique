package com.ehei.auth.controller;

import com.ehei.auth.DTOS.ApiResponse;
import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.Staff.StaffRegistrationRequest;
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
    public ResponseEntity<ApiResponse<StaffAccountResult>> registerStaff(@Valid @RequestBody StaffRegistrationRequest request) {

        StaffAccountResult result = authService.createStaffWithKeycloakAccount(request);

        return ResponseEntity.ok(
                ApiResponse.success(result, "Staff et compte créés avec succès")
        );
    }



}
