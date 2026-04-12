package com.ehei.staff.controllers;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.DTOS.Staff.StaffUpdateRequestDto;
import com.ehei.staff.services.StaffService;
import com.ehei.staff.utils.SecurityUtils;
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
@RequestMapping("/staff")
@Tag(name = "Staff Management", description = "APIs for managing staff member records")
public class StaffController {

    private final StaffService staffService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new staff member",
            description = "Creates a new staff member record in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Staff member successfully created",
                    content = @Content(schema = @Schema(implementation = StaffResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Staff member already exists",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<StaffResponseDto>> create(
            @RequestBody @Valid StaffCreateRequestDto staffdto
    ){
        StaffResponseDto response = staffService.CreateStaff(staffdto);
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response,
                "staff creee avec succes."));
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all staff members",
            description = "Retrieves a list of all staff members in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all staff members",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<List<StaffResponseDto>>> getAllStaff(){
        List<StaffResponseDto> response = staffService.getAll();
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get staff member by ID",
            description = "Retrieves a specific staff member by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved staff member",
                    content = @Content(schema = @Schema(implementation = StaffResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Staff member not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<StaffResponseDto>> getStaffById(
            @Parameter(description = "ID of the staff member to retrieve", required = true)
            @PathVariable String id
    ){
        StaffResponseDto response = staffService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response));
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update staff member information",
            description = "Updates an existing staff member's information by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Staff member successfully updated",
                    content = @Content(schema = @Schema(implementation = StaffResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Staff member not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<StaffResponseDto>> updateStaff(
            @Parameter(description = "ID of the staff member to update", required = true)
            @PathVariable String id,
            @RequestBody @Valid StaffUpdateRequestDto staffdto
    ){
        StaffResponseDto response = staffService.update(Long.valueOf(id), staffdto);
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response,
                "staff modifie avec succes."));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete staff member",
            description = "Deletes a staff member record by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Staff member successfully deleted",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Staff member not found",
                    content = @Content
            )
    })
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<String>> deleteStaff(
            @Parameter(description = "ID of the staff member to delete", required = true)
            @PathVariable String id
    ){
        staffService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success("staff supprime avec succes."));
    }

    // StaffController — ajouter cet endpoint

    @GetMapping("/me")
    public ResponseEntity<com.ehei.staff.DTOS.ApiResponse<StaffResponseDto>> getMe(
            @RequestHeader("X-Internal-Id") Long id,
            @RequestHeader("X-User-Roles") String rolesHeader
    ) {
        SecurityUtils.requireRole(rolesHeader, "STAFF");
        StaffResponseDto response = staffService.getById(id);
        return ResponseEntity.ok().body(com.ehei.staff.DTOS.ApiResponse.success(response));
    }
}