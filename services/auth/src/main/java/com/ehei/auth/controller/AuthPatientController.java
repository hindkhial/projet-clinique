package com.ehei.auth.controller;

import com.ehei.auth.DTOS.ApiResponse;
import com.ehei.auth.DTOS.patient.PatientAccountResult;
import com.ehei.auth.DTOS.patient.PatientRegistrationRequest;
import com.ehei.auth.DTOS.patient.PatientUpdateRequestAuth;
import com.ehei.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/patient")
@RequiredArgsConstructor
@Tag(name = "Patient Authentication", description = "APIs for managing patient authentication and registration with Keycloak integration")
public class AuthPatientController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new patient", description = "Creates a new patient account and registers it with Keycloak")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient successfully registered",
                    content = @Content(schema = @Schema(implementation = PatientAccountResult.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Patient already exists", content = @Content)
    })
    public ResponseEntity<ApiResponse<PatientAccountResult>> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request
    ) {
        PatientAccountResult result = authService.createPatientWithKeycloakAccount(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Patient et compte créés avec succès"));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update patient information", description = "Updates an existing patient and synchronizes with Keycloak")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient successfully updated",
                    content = @Content(schema = @Schema(implementation = PatientAccountResult.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<PatientAccountResult>> updatePatient(
            @Parameter(description = "ID of the patient to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateRequestAuth request
    ) {
        PatientAccountResult result = authService.updatePatientWithKeycloakAccount(id, request);
        return ResponseEntity.ok(ApiResponse.success(result, "Patient et compte mis à jour avec succès"));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete patient account", description = "Deletes a patient account and removes it from Keycloak")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient successfully deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<String>> deletePatient(
            @Parameter(description = "ID of the patient to delete", required = true)
            @PathVariable Long id
    ) {
        authService.deletePatientWithKeycloakAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Patient et compte supprimés avec succès"));
    }
}