package com.ehei.auth.mapper;

import com.ehei.auth.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.auth.DTOS.Staff.StaffRegistrationRequest;
import com.ehei.auth.DTOS.Staff.StaffUpdateRequestAuth;
import com.ehei.auth.DTOS.Staff.StaffUpdateRequestDto;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class StaffMapper {

    public StaffCreateRequestDto toStaffCreateRequestDto(StaffRegistrationRequest staffRegistrationRequest) {
        StaffCreateRequestDto staffDto = new StaffCreateRequestDto();
        staffDto.setNom(staffRegistrationRequest.getNom());
        staffDto.setPrenom(staffRegistrationRequest.getPrenom());
        staffDto.setEmail(staffRegistrationRequest.getEmail());
        staffDto.setRole(staffRegistrationRequest.getRole());
        staffDto.setDateNaissance(staffRegistrationRequest.getDateNaissance());
        staffDto.setTel(staffRegistrationRequest.getTel());
        return staffDto;
    }

    public StaffUpdateRequestDto toStaffUpdateRequestDto(StaffUpdateRequestAuth updateRequest) {
        StaffUpdateRequestDto staffDto = new StaffUpdateRequestDto();
        staffDto.setNom(updateRequest.getNom());
        staffDto.setPrenom(updateRequest.getPrenom());
        staffDto.setEmail(updateRequest.getEmail());
        staffDto.setRole(updateRequest.getRole());
        staffDto.setDateNaissance(updateRequest.getDateNaissance());
        staffDto.setTel(updateRequest.getTel());
        return staffDto;
    }
}
