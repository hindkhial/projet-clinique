package com.ehei.auth.controller;

import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.auth.DTOS.Staff.StaffRegistrationRequest;
import com.ehei.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/staff")
@RequiredArgsConstructor
public class AuthStaffController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerStaff(@Valid @RequestBody StaffRegistrationRequest request) {
        try {
            StaffCreateRequestDto staffDto = new StaffCreateRequestDto();
            staffDto.setNom(request.getNom());
            staffDto.setPrenom(request.getPrenom());
            staffDto.setEmail(request.getEmail());
            staffDto.setRole(request.getRole());
            staffDto.setDateNaissance(request.getDateNaissance());
            staffDto.setTel(request.getTel());

            StaffAccountResult result = authService.createStaffWithKeycloakAccount(
                    staffDto,
                    request.getPassword()
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
