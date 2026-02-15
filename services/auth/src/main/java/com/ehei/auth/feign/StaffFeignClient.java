package com.ehei.auth.feign;

import com.ehei.auth.DTOS.ApiResponse;
import com.ehei.auth.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.auth.DTOS.Staff.StaffResponseDto;
import com.ehei.auth.DTOS.Staff.StaffUpdateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "STAFF-SERVICE",
        url = "${staff.service.url}"
)
public interface StaffFeignClient {
    @PostMapping("/staff/create")
    ApiResponse<StaffResponseDto> createStaff(@RequestBody StaffCreateRequestDto request);

    @GetMapping("/staff/{id}")
    ApiResponse<StaffResponseDto> getStaffById(@PathVariable("id") Long id);

    @DeleteMapping("/staff/delete/{id}")
    ApiResponse<String> deleteStaff(@PathVariable("id") Long id);

    @PutMapping("/staff/update/{id}")
    ApiResponse<StaffResponseDto> updateStaff(@PathVariable String id,
                                              @RequestBody StaffUpdateRequestDto staffdto);
}
