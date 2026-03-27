package com.ehei.auth.controller;

import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.medecin.MedecinRegistrationRequest;
import com.ehei.auth.DTOS.medecin.MedecinUpdateRequestAuth;
import com.ehei.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/medecin")
@RequiredArgsConstructor
@Tag(name = "Médecin Authentication", description = "APIs for managing médecin authentication and registration with Keycloak integration")
public class AuthMedecinController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new médecin",
            description = "Creates a new médecin account and registers it with Keycloak authentication system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Médecin successfully registered",
                    content = @Content(schema = @Schema(implementation = StaffAccountResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Médecin already exists",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.auth.DTOS.ApiResponse<StaffAccountResult>> registerMedecin(
            @Valid @RequestBody MedecinRegistrationRequest request
    ) {
        StaffAccountResult result = authService.createMedecinWithKeycloakAccount(request);

        return ResponseEntity.ok(
                com.ehei.auth.DTOS.ApiResponse.success(result, "Médecin et compte créés avec succès")
        );
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update médecin information",
            description = "Updates an existing médecin's information and synchronizes with Keycloak account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Médecin successfully updated",
                    content = @Content(schema = @Schema(implementation = StaffAccountResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Médecin not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.auth.DTOS.ApiResponse<StaffAccountResult>> updateMedecin(
            @Parameter(description = "ID of the médecin to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody MedecinUpdateRequestAuth request
    ) {
        StaffAccountResult result = authService.updateMedecinWithKeycloakAccount(id, request);

        return ResponseEntity.ok(
                com.ehei.auth.DTOS.ApiResponse.success(result, "Médecin et compte mis à jour avec succès")
        );
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete médecin account",
            description = "Deletes a médecin account and removes it from Keycloak authentication system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Médecin successfully deleted",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Médecin not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error deleting médecin or Keycloak account",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.auth.DTOS.ApiResponse<String>> deleteMedecin(
            @Parameter(description = "ID of the médecin to delete", required = true)
            @PathVariable Long id
    ) {
        authService.deleteMedecinWithKeycloakAccount(id);

        return ResponseEntity.ok(
                com.ehei.auth.DTOS.ApiResponse.success("Médecin et compte supprimés avec succès")
        );
    }
}