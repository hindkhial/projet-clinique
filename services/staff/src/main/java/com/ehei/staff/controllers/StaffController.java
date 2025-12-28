package com.ehei.staff.controllers;

import com.ehei.staff.DTOS.ApiResponse;
import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.DTOS.Staff.StaffUpdateRequestDto;
import com.ehei.staff.services.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StaffResponseDto>> create(@RequestBody @Valid StaffCreateRequestDto staffdto){
        StaffResponseDto response = staffService.CreateStaff(staffdto);
        return ResponseEntity.ok().body(ApiResponse.success(response,
                "staff creee avec succes."));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<StaffResponseDto>>> getAllStaff(){
        List<StaffResponseDto> response = staffService.getAll();
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponseDto>> getStaffById(@PathVariable String id){
        StaffResponseDto response = staffService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<StaffResponseDto>> updateStaff(@PathVariable String id,
                                                                     @RequestBody @Valid StaffUpdateRequestDto staffdto){
        StaffResponseDto response = staffService.update(Long.valueOf(id), staffdto);
        return ResponseEntity.ok().body(ApiResponse.success(response,
                "staff modifie avec succes."));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteStaff(@PathVariable String id){
        staffService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body(ApiResponse.success("staff supprime avec succes."));
    }
}
