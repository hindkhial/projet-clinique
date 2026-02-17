package com.ehei.auth.controller;

import com.ehei.auth.DTOS.Staff.StaffAccountResult;
import com.ehei.auth.DTOS.Staff.StaffRegistrationRequest;
import com.ehei.auth.DTOS.Staff.StaffUpdateRequestAuth;
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
@RequestMapping("/auth/staff")
@RequiredArgsConstructor
@Tag(name = "Staff Authentication", description = "APIs for managing staff authentication and registration with Keycloak integration")
public class AuthStaffController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new staff member",
            description = "Creates a new staff account and registers it with Keycloak authentication system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Staff successfully registered",
                    content = @Content(schema = @Schema(implementation = StaffAccountResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Staff already exists",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.auth.DTOS.ApiResponse<StaffAccountResult>> registerStaff(
            @Valid @RequestBody StaffRegistrationRequest request
    ) {
        StaffAccountResult result = authService.createStaffWithKeycloakAccount(request);

        return ResponseEntity.ok(
                com.ehei.auth.DTOS.ApiResponse.success(result, "Staff et compte créés avec succès")
        );
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update staff information",
            description = "Updates an existing staff member's information and synchronizes with Keycloak account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Staff successfully updated",
                    content = @Content(schema = @Schema(implementation = StaffAccountResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Staff not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.auth.DTOS.ApiResponse<StaffAccountResult>> updateStaff(
            @Parameter(description = "ID of the staff member to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody StaffUpdateRequestAuth request
    ) {
        StaffAccountResult result = authService.updateStaffWithKeycloakAccount(id, request);

        return ResponseEntity.ok(
                com.ehei.auth.DTOS.ApiResponse.success(result, "Staff et compte mis à jour avec succès")
        );
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete staff account",
            description = "Deletes a staff account and removes it from Keycloak authentication system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Staff successfully deleted",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Staff not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error deleting staff or Keycloak account",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.auth.DTOS.ApiResponse<String>> deleteStaff(
            @Parameter(description = "ID of the staff member to delete", required = true)
            @PathVariable Long id
    ) {
        authService.deleteStaffWithKeycloakAccount(id);

        return ResponseEntity.ok(
                com.ehei.auth.DTOS.ApiResponse.success("Staff et compte supprimés avec succès")
        );
    }
}