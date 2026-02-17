package com.ehei.consultation;

import com.ehei.consultation.dtos.ConsultationCreateRequest;
import com.ehei.consultation.dtos.ConsultationResponse;
import com.ehei.consultation.dtos.ConsultationUpdateRequest;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/consultation")
@Tag(name = "Consultation Management", description = "APIs for managing medical consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new consultation",
            description = "Creates a new medical consultation record"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultation successfully created",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.consultation.dtos.ApiResponse<ConsultationResponse>> create(
            @RequestBody @Valid ConsultationCreateRequest dto
    ) {
        ConsultationResponse response = consultationService.create(dto);
        return ResponseEntity.ok(
                com.ehei.consultation.dtos.ApiResponse.success(response, "consultation creee avec succes.")
        );
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all consultations",
            description = "Retrieves a list of all consultations"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all consultations",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<com.ehei.consultation.dtos.ApiResponse<List<ConsultationResponse>>> getAll() {
        return ResponseEntity.ok(
                com.ehei.consultation.dtos.ApiResponse.success(consultationService.getAll())
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get consultation by ID",
            description = "Retrieves a specific consultation by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved consultation",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consultation not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.consultation.dtos.ApiResponse<ConsultationResponse>> getById(
            @Parameter(description = "ID of the consultation to retrieve", required = true)
            @PathVariable String id
    ) {
        return ResponseEntity.ok(
                com.ehei.consultation.dtos.ApiResponse.success(consultationService.getById(Long.valueOf(id)))
        );
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update consultation",
            description = "Updates an existing consultation by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultation successfully updated",
                    content = @Content(schema = @Schema(implementation = ConsultationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consultation not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.consultation.dtos.ApiResponse<ConsultationResponse>> update(
            @Parameter(description = "ID of the consultation to update", required = true)
            @PathVariable String id,
            @RequestBody @Valid ConsultationUpdateRequest dto
    ) {
        ConsultationResponse response = consultationService.update(Long.valueOf(id), dto);
        return ResponseEntity.ok(
                com.ehei.consultation.dtos.ApiResponse.success(response, "consultation modifiee avec succes.")
        );
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete consultation",
            description = "Deletes a consultation by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Consultation successfully deleted",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consultation not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.consultation.dtos.ApiResponse<String>> delete(
            @Parameter(description = "ID of the consultation to delete", required = true)
            @PathVariable String id
    ) {
        consultationService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok(
                com.ehei.consultation.dtos.ApiResponse.success("consultation supprimee avec succes.")
        );
    }
}