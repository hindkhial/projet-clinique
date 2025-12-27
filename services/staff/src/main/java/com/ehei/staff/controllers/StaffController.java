package com.ehei.staff.controllers;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.DTOS.Staff.StaffUpdateRequestDto;
import com.ehei.staff.models.Staff;
import com.ehei.staff.services.StaffService;
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
    public ResponseEntity<StaffResponseDto> create(@RequestBody StaffCreateRequestDto staffdto){
        StaffResponseDto response = staffService.CreateStaff(staffdto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<StaffResponseDto>> getAllStaff(){
        List<StaffResponseDto> response = staffService.getAll();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDto> getStaffById(@PathVariable String id){
        StaffResponseDto response = staffService.getById(Long.valueOf(id));
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<StaffResponseDto> updateStaff(@PathVariable String id, @RequestBody StaffUpdateRequestDto staffdto){
        StaffResponseDto response = staffService.update(Long.valueOf(id), staffdto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStaff(@PathVariable String id){
        staffService.deleteById(Long.valueOf(id));
        return ResponseEntity.ok().body("Staff has been deleted");
    }
}
