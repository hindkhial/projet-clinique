package com.ehei.rapport.controller;

import com.ehei.rapport.dto.CreateRapportRequest;
import com.ehei.rapport.dto.RapportResponse;
import com.ehei.rapport.service.MinioService;
import com.ehei.rapport.service.RapportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/rapport")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rapport Management", description = "APIs for managing medical reports and PDF generation")
public class RapportController {

    private final RapportService rapportService;
    private final MinioService minioService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new medical report",
            description = "Creates a new medical report for a patient"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Report successfully created",
                    content = @Content(schema = @Schema(implementation = RapportResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient or medecin not found",
                    content = @Content
            )
    })
    public ResponseEntity<RapportResponse> createRapport(
            @Valid @RequestBody CreateRapportRequest request
    ){
        log.info("POST /rapport/create - creation d'un rapport pour patient: {}", request.getPatientId());
        RapportResponse rapportResponse = rapportService.createRapport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rapportResponse);
    }

    @GetMapping("/patient/{id}")
    @Operation(
            summary = "Get reports by patient",
            description = "Retrieves all medical reports associated with a specific patient"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved patient's reports",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content
            )
    })
    public ResponseEntity<List<RapportResponse>> getRapportsByPatient(
            @Parameter(description = "ID of the patient", required = true)
            @PathVariable Long id
    ){
        log.info("GET /rapport/patient/{} - recuperation des rapports du patient", id);
        List<RapportResponse> rapports = rapportService.getRapportsByPatient(id);
        return ResponseEntity.ok(rapports);
    }

    @GetMapping("/medecin/{id}")
    @Operation(
            summary = "Get reports by medecin",
            description = "Retrieves all medical reports created by a specific medecin"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved medecin's reports",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Medecin not found",
                    content = @Content
            )
    })
    public ResponseEntity<List<RapportResponse>> getRapportsByMedcin(
            @Parameter(description = "ID of the medecin", required = true)
            @PathVariable Long id
    ){
        log.info("GET /rapport/medecin/{} - recuperation des rapports du medecin", id);
        List<RapportResponse> rapports = rapportService.getRapportsByMedecin(id);
        return ResponseEntity.ok(rapports);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get report by ID",
            description = "Retrieves a specific medical report by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved report",
                    content = @Content(schema = @Schema(implementation = RapportResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report not found",
                    content = @Content
            )
    })
    public ResponseEntity<RapportResponse> getRapportById(
            @Parameter(description = "ID of the report to retrieve", required = true)
            @PathVariable Long id
    ){
        log.info("GET /rapport/{} - recuperation de rapport", id);
        RapportResponse rapport = rapportService.getRapportById(id);
        return ResponseEntity.ok(rapport);
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all reports",
            description = "Retrieves all medical reports in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all reports",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<RapportResponse>> getAllRapport(){
        log.info("GET /rapport/ - recuperation de tous les rapports");
        List<RapportResponse> rapports = rapportService.getAllRapports();
        return ResponseEntity.ok(rapports);
    }

    @PostMapping("/{id}/generatepdf")
    @Operation(
            summary = "Generate PDF for report",
            description = "Generates a PDF document for an existing medical report and stores it in MinIO"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF successfully generated",
                    content = @Content(schema = @Schema(implementation = RapportResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error generating or storing PDF",
                    content = @Content
            )
    })
    public ResponseEntity<RapportResponse> generatePdf(
            @Parameter(description = "ID of the report to generate PDF for", required = true)
            @PathVariable Long id
    ) {
        log.info("POST /rapport/{}/generatepdf", id);
        RapportResponse rapport = rapportService.generatePdf(id);
        return ResponseEntity.ok(rapport);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete report",
            description = "Deletes a medical report and its associated PDF file from storage"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Report successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error deleting report or PDF file",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteRapport(
            @Parameter(description = "ID of the report to delete", required = true)
            @PathVariable Long id
    ) {
        log.info("DELETE rapport/{}", id);
        rapportService.deleteRapport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    @Operation(
            summary = "Download report PDF",
            description = "Downloads the PDF file of a medical report from MinIO storage"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully downloaded PDF",
                    content = @Content(mediaType = "application/pdf")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report or PDF file not found",
                    content = @Content
            )
    })
    public ResponseEntity<InputStreamResource> downloadPdf(
            @Parameter(description = "ID of the report to download PDF for", required = true)
            @PathVariable Long id
    ) {
        log.info("GET /rapport/{}/download", id);

        // Get rapport
        RapportResponse rapport = rapportService.getRapportById(id);

        // verifier si rapport exist
        if (rapport.getPdfFileUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        // Download PDF from MinIO
        InputStream pdfStream = minioService.downloadPdf(rapport.getPdfFileUrl());

        // Return PDF file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }

    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Checks if the Rapport service is running"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Rapport Service is running!");
    }
}