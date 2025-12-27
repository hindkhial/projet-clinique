package com.ehei.staff.mappers;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.models.Staff;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StaffMapper {
    public Staff toModel(StaffCreateRequestDto dto) {
        return Staff.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .role(dto.getRole())
                .tel(dto.getTel())
                .dateNaissance(dto.getDateNaissance())
                .build();
    }

    public StaffResponseDto toResponseDto(Staff staff) {
        return StaffResponseDto.builder()
                .id(staff.getId())
                .nom(staff.getNom())
                .prenom(staff.getPrenom())
                .email(staff.getEmail())
                .tel(staff.getTel())
                .dateNaissance(staff.getDateNaissance())
                .role(staff.getRole())
                .build();
    }
}
