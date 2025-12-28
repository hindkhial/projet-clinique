package com.ehei.staff.mappers;

import com.ehei.staff.DTOS.medecin.MedecinCreateRequestDto;
import com.ehei.staff.DTOS.medecin.MedecinResponseDto;
import com.ehei.staff.DTOS.medecin.MedecinUpdateRequestDto;
import com.ehei.staff.models.Medecin;
import com.ehei.staff.models.Staff;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MedecinMapper {
    public Medecin toModel(MedecinCreateRequestDto requestDto, Staff staff) {
        return Medecin.builder()
                .staff(staff)
                .specialite(requestDto.getSpecialite())
                .build();
    }

    public MedecinResponseDto toResponseDto(Medecin medecin) {
        Staff staff = medecin.getStaff();
        return MedecinResponseDto.builder()
                .id(medecin.getId())
                .tel(staff.getTel())
                .email(staff.getEmail())
                .dateNaissance(staff.getDateNaissance())
                .specialite(medecin.getSpecialite())
                .nom(staff.getNom())
                .prenom(staff.getPrenom())
                .staffId(staff.getId())
                .role(staff.getRole())
                .build();
    }

    public List<MedecinResponseDto> toResponseDto(List<Medecin> medecins) {
        List<MedecinResponseDto> response = new ArrayList<>();
        for (Medecin medecin : medecins) {
            response.add(toResponseDto(medecin));
        }
        return response;
    }

    public Medecin toModel(MedecinUpdateRequestDto requestDto, Staff staff) {
        return Medecin.builder()
                .staff(staff)
                .specialite(requestDto.getSpecialite())
                .build();
    }
}
