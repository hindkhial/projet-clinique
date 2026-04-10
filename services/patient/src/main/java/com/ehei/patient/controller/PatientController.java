package com.ehei.patient.controller;

import com.ehei.patient.dto.PatientCreateRequeste;
import com.ehei.patient.dto.PatientResponse;
import com.ehei.patient.dto.PatientUpdateRequeste;
import com.ehei.patient.service.PatientService;
import com.ehei.patient.utils.SecurityUtils;
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

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RestController
@RequestMapping("/patient")
@Tag(name = "Patient Management", description = "APIs for managing patient records")
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new patient",
            description = "Creates a new patient record in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient successfully created",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.patient.dto.ApiResponse<PatientResponse>> create(
            @RequestHeader("X-User-Roles") String rolesHeader,
            @RequestBody @Valid PatientCreateRequeste patientDto
    ){
        SecurityUtils.requireRole(rolesHeader, "STAFF");
        PatientResponse response = patientService.createPatient(patientDto);
        return ResponseEntity.ok().body(com.ehei.patient.dto.ApiResponse.success(response,
                "patient creee avec succes."));
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all patients",
            description = "Retrieves a list of all patients in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all patients",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<com.ehei.patient.dto.ApiResponse<List<PatientResponse>>> getAllPatients(
            @RequestHeader("X-User-Roles") String rolesHeader
    ){
        SecurityUtils.requireRole(rolesHeader, "STAFF");
        List<PatientResponse> response = patientService.getAll();
        return ResponseEntity.ok().body(com.ehei.patient.dto.ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get patient by ID",
            description = "Retrieves a specific patient by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved patient",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.patient.dto.ApiResponse<PatientResponse>> getPatientById(
            @Parameter(description = "ID of the patient to retrieve", required = true)
            @PathVariable String id,
            @RequestHeader("X-User-Roles") String rolesHeader

    ){
        SecurityUtils.requireRole(rolesHeader, "STAFF");
        PatientResponse response = patientService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(com.ehei.patient.dto.ApiResponse.success(response));
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update patient information",
            description = "Updates an existing patient's information by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient successfully updated",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.patient.dto.ApiResponse<PatientResponse>> updatePatient(
            @RequestHeader("X-User-Roles") String rolesHeader,
            @Parameter(description = "ID of the patient to update", required = true)
            @PathVariable String id,
            @RequestBody @Valid PatientUpdateRequeste patientDto
    ){
        PatientResponse response = patientService.update(Long.valueOf(id), patientDto);
        return ResponseEntity.ok().body(com.ehei.patient.dto.ApiResponse.success(response,
                "Patient modifie avec succes."));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete patient",
            description = "Deletes a patient record by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient successfully deleted",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.patient.dto.ApiResponse<String>> deletePatient(
            @Parameter(description = "ID of the patient to delete", required = true)
            @PathVariable String id,
            @RequestHeader("X-User-Roles") String rolesHeader

    ){
        SecurityUtils.requireRole(rolesHeader, "STAFF");
        patientService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body(com.ehei.patient.dto.ApiResponse.success("patient supprime avec succes."));
    }

    @GetMapping("/search")
    public ResponseEntity<com.ehei.patient.dto.ApiResponse<List<PatientResponse>>> searchPatients(
            @RequestParam String q,
            @RequestHeader("X-User-Roles") String rolesHeader
    ) {
        SecurityUtils.requireRole(rolesHeader, "STAFF");
        List<PatientResponse> response = patientService.searchPatients(q);
        return ResponseEntity.ok().body(com.ehei.patient.dto.ApiResponse.success(response));
    }

    // ==================== PATIENT : son propre profil ====================

    @GetMapping("/me")
    public ResponseEntity<com.ehei.patient.dto.ApiResponse<PatientResponse>> getMe(
            @RequestHeader("X-Internal-Id") Long id,
            @RequestHeader("X-User-Roles") String rolesHeader
    ) {
        SecurityUtils.requireRole(rolesHeader, "PATIENT");
        PatientResponse response = patientService.getById(id);
        return ResponseEntity.ok(com.ehei.patient.dto.ApiResponse.success(response));
    }
}