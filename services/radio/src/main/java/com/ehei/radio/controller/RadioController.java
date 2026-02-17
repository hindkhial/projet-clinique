package com.ehei.radio.controller;

import com.ehei.radio.dto.CreateRadioRequest;
import com.ehei.radio.dto.RadioResponse;
import com.ehei.radio.service.MinioService;
import com.ehei.radio.service.RadioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/Radio")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Radio Management", description = "APIs for managing radiology images and reports")
public class RadioController {

    private final RadioService radioService;
    private final MinioService minioService;
    private final ObjectMapper objectMapper;

    @PostMapping("/Upload")
    @Operation(
            summary = "Upload a new radiology image",
            description = "Uploads a radiology image file along with associated metadata to MinIO storage"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Radio successfully uploaded",
                    content = @Content(schema = @Schema(implementation = RadioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or file format",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error uploading radio to storage",
                    content = @Content
            )
    })
    public ResponseEntity<RadioResponse> uploadRadio(
            @Parameter(description = "Radio metadata as JSON string", required = true)
            @RequestParam("radioData") String radioDataJson,
            @Parameter(description = "Radiology image file", required = true)
            @RequestParam("file") MultipartFile file) {

        log.info("POST /Radio/Upload - Upload d'un nouveau radio");

        try {
            CreateRadioRequest request = objectMapper.readValue(radioDataJson, CreateRadioRequest.class);
            RadioResponse response = radioService.createRadio(request, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erreur lors de la creation du radio: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la creation du radio", e);
        }
    }

    @GetMapping("/Patient/{id}")
    @Operation(
            summary = "Get radios by patient",
            description = "Retrieves all radiology images associated with a specific patient"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved patient's radios",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content
            )
    })
    public ResponseEntity<List<RadioResponse>> getRadiosByPatient(
            @Parameter(description = "ID of the patient", required = true)
            @PathVariable Long id) {
        log.info("GET /Radio/Patient/{} - Recuperation des radios du patient", id);

        List<RadioResponse> radios = radioService.getRadiosByPatient(id);
        return ResponseEntity.ok(radios);
    }

    @GetMapping("/Radiologue/{id}")
    @Operation(
            summary = "Get radios by radiologist",
            description = "Retrieves all radiology images assigned to a specific radiologist"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved radiologist's radios",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Radiologist not found",
                    content = @Content
            )
    })
    public ResponseEntity<List<RadioResponse>> getRadiosByRadiologue(
            @Parameter(description = "ID of the radiologist", required = true)
            @PathVariable Long id) {
        log.info("GET /Radio/Radiologue/{} - Recuperation des radios du radiologue", id);

        List<RadioResponse> radios = radioService.getRadiosByRadiologue(id);
        return ResponseEntity.ok(radios);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get radio by ID",
            description = "Retrieves a specific radiology record by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved radio",
                    content = @Content(schema = @Schema(implementation = RadioResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Radio not found",
                    content = @Content
            )
    })
    public ResponseEntity<RadioResponse> getRadioById(
            @Parameter(description = "ID of the radio to retrieve", required = true)
            @PathVariable Long id) {
        log.info("GET /Radio/{} - Recuperation du radio", id);

        RadioResponse radio = radioService.getRadioById(id);
        return ResponseEntity.ok(radio);
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all radios",
            description = "Retrieves all radiology records in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all radios",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<RadioResponse>> getAllRadios() {
        log.info("GET /Radio/ - Recuperation de tous les radios");

        List<RadioResponse> radios = radioService.getAllRadios();
        return ResponseEntity.ok(radios);
    }

    @GetMapping("/{id}/download")
    @Operation(
            summary = "Download radio image",
            description = "Downloads the radiology image file from MinIO storage"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully downloaded radio image",
                    content = @Content(mediaType = "image/jpeg")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Radio or image file not found",
                    content = @Content
            )
    })
    public ResponseEntity<InputStreamResource> downloadRadioImage(
            @Parameter(description = "ID of the radio to download", required = true)
            @PathVariable Long id) {
        log.info("GET /Radio/{}/download - Telechargement de l'image radio", id);

        RadioResponse radio = radioService.getRadioById(id);

        if (radio.getFichierRadioUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        InputStream imageStream = minioService.downloadRadioImage(radio.getFichierRadioUrl());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=radio_" + id + ".jpg")
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(imageStream));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete radio",
            description = "Deletes a radiology record and its associated image file from storage"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Radio successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Radio not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error deleting radio or image file",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteRadio(
            @Parameter(description = "ID of the radio to delete", required = true)
            @PathVariable Long id) {
        log.info("DELETE /Radio/{} - Suppression du radio", id);

        radioService.deleteRadio(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Checks if the Radio service is running"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Radio Service is running");
    }
}