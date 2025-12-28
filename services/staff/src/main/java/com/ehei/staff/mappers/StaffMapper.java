package com.ehei.staff.mappers;

import com.ehei.staff.DTOS.Staff.StaffCreateRequestDto;
import com.ehei.staff.DTOS.Staff.StaffResponseDto;
import com.ehei.staff.DTOS.Staff.StaffUpdateRequestDto;
import com.ehei.staff.DTOS.medecin.MedecinCreateRequestDto;
import com.ehei.staff.DTOS.medecin.MedecinUpdateRequestDto;
import com.ehei.staff.enums.Role;
import com.ehei.staff.models.Staff;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<StaffResponseDto> toResponseDto(List<Staff> staffListe) {
        List<StaffResponseDto> DtoList = new ArrayList<>();
        for (Staff staff : staffListe) {
            DtoList.add(toResponseDto(staff));
        }
        return DtoList;
    }

    public Staff toModel(MedecinCreateRequestDto requestdto) {
        return Staff.builder()
                .nom(requestdto.getNom())
                .prenom(requestdto.getPrenom())
                .email(requestdto.getEmail())
                .role(Role.MEDECIN)
                .tel(requestdto.getTel())
                .dateNaissance(requestdto.getDateNaissance())
                .build();
    }

    public Staff toModel(StaffUpdateRequestDto dto) {

        return Staff.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .role(dto.getRole())
                .tel(dto.getTel())
                .dateNaissance(dto.getDateNaissance())
                .build();
    }

    public Staff toModel(MedecinUpdateRequestDto requestdto) {
        return Staff.builder()
                .nom(requestdto.getNom())
                .prenom(requestdto.getPrenom())
                .email(requestdto.getEmail())
                .role(Role.MEDECIN)
                .tel(requestdto.getTel())
                .dateNaissance(requestdto.getDateNaissance())
                .build();
    }
}
