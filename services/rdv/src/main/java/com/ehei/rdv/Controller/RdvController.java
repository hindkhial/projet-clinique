package com.ehei.rdv.Controller;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import com.ehei.rdv.DTO.RdvUpdateRequestDto;
import com.ehei.rdv.Service.RdvService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rdv")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous Management", description = "APIs for managing medical appointments (rendez-vous)")
public class RdvController {

    private final RdvService rdvService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new appointment",
            description = "Creates a new rendez-vous (appointment) in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment successfully created",
                    content = @Content(schema = @Schema(implementation = RdvResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Appointment time conflict",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.rdv.DTO.ApiResponse<RdvResponseDto>> createRdv(
            @RequestBody RdvCreateRequestDto requestDto
    ){
        RdvResponseDto response = rdvService.createRdv(requestDto);
        return ResponseEntity.ok().body(com.ehei.rdv.DTO.ApiResponse.success(response,"rdv creee avec succes."));
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update appointment",
            description = "Updates an existing rendez-vous by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment successfully updated",
                    content = @Content(schema = @Schema(implementation = RdvResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Appointment time conflict",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.rdv.DTO.ApiResponse<RdvResponseDto>> updateRdv(
            @Parameter(description = "ID of the appointment to update", required = true)
            @PathVariable String id,
            @RequestBody RdvUpdateRequestDto requestDto
    ){
        RdvResponseDto response = rdvService.updateRdv(Long.valueOf(id), requestDto);
        return ResponseEntity.ok().body(com.ehei.rdv.DTO.ApiResponse.success(response,"rdv modifie avec succes"));
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all appointments",
            description = "Retrieves all rendez-vous in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all appointments",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<com.ehei.rdv.DTO.ApiResponse<List<RdvResponseDto>>> getAll(){
        List<RdvResponseDto> response = rdvService.getAll();
        return ResponseEntity.ok().body(com.ehei.rdv.DTO.ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get appointment by ID",
            description = "Retrieves a specific rendez-vous by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved appointment",
                    content = @Content(schema = @Schema(implementation = RdvResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.rdv.DTO.ApiResponse<RdvResponseDto>> getById(
            @Parameter(description = "ID of the appointment to retrieve", required = true)
            @PathVariable String id
    ){
        RdvResponseDto response = rdvService.getById(Long.valueOf(id));
        return  ResponseEntity.ok().body(com.ehei.rdv.DTO.ApiResponse.success(response)) ;
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete appointment",
            description = "Deletes a rendez-vous by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment successfully deleted",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.rdv.DTO.ApiResponse<String>> deleteById(
            @Parameter(description = "ID of the appointment to delete", required = true)
            @PathVariable String id
    ){
        rdvService.deleteById(Long.valueOf(id));
        return  ResponseEntity.ok().body(com.ehei.rdv.DTO.ApiResponse.success("rdv supprime avec succes."));
    }
}