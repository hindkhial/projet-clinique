package com.ehei.staff.controllers;

import com.ehei.staff.DTOS.medecin.MedecinCreateRequestDto;
import com.ehei.staff.DTOS.medecin.MedecinResponseDto;
import com.ehei.staff.DTOS.medecin.MedecinUpdateRequestDto;
import com.ehei.staff.services.MedecinService;
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

@RestController
@RequestMapping("/medecin")
@RequiredArgsConstructor
@Tag(name = "Médecin Management", description = "APIs for managing médecin (doctor) records")
public class MedecinController {

    private final MedecinService medecinService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new médecin",
            description = "Creates a new médecin record in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Médecin successfully created",
                    content = @Content(schema = @Schema(implementation = MedecinResponseDto.class))
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
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<MedecinResponseDto>> createMedecin(
            @RequestBody @Valid MedecinCreateRequestDto requestDto
    ){
        MedecinResponseDto response = medecinService.createMedecin(requestDto);
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response,"medecin creee avec succes."));
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all médecins",
            description = "Retrieves a list of all médecins in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all médecins",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<List<MedecinResponseDto>>> getAllMedecins(){
        List<MedecinResponseDto> response = medecinService.getAll();
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get médecin by ID",
            description = "Retrieves a specific médecin by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved médecin",
                    content = @Content(schema = @Schema(implementation = MedecinResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Médecin not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<MedecinResponseDto>> getMedecinById(
            @Parameter(description = "ID of the médecin to retrieve", required = true)
            @PathVariable String id
    ){
        MedecinResponseDto response = medecinService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete médecin",
            description = "Deletes a médecin record by their ID"
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
            )
    })
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<String>> deleteMedecinById(
            @Parameter(description = "ID of the médecin to delete", required = true)
            @PathVariable String id
    ){
        medecinService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success("Medecin supprime avec succes"));
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update médecin information",
            description = "Updates an existing médecin's information by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Médecin successfully updated",
                    content = @Content(schema = @Schema(implementation = MedecinResponseDto.class))
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
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<MedecinResponseDto>> updateMedecinById(
            @Parameter(description = "ID of the médecin to update", required = true)
            @PathVariable String id,
            @RequestBody @Valid MedecinUpdateRequestDto requestDto
    ){
        MedecinResponseDto response = medecinService.updateById(Long.valueOf(id), requestDto);
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response,"Medecin modifie avec succes."));
    }

}