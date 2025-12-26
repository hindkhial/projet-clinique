package com.ehei.staff.controllers;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.models.Staff;
import com.ehei.staff.services.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
