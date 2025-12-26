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
                .role(dto.getRole())
                .tel(dto.getTel())
                .build();
    }

    public StaffResponseDto toResponseDto(Staff staff) {
        return StaffResponseDto.builder()
                .id(staff.getId())
                .nom(staff.getNom())
                .prenom(staff.getPrenom())
                .tel(staff.getTel())
                .role(staff.getRole())
                .build();
    }
}
