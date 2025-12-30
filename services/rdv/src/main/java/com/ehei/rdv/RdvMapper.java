package com.ehei.rdv;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;

public class RdvMapper {
    public Rdv toModel(RdvCreateRequestDto requestDto) {
        return Rdv.builder()
                    .idPatient(requestDto.getIdPatient())
                    .idMedecin(requestDto.getIdMedecin())
                    .dateRdv(requestDto.getDateRdv())
                    .build();
    }

    public RdvResponseDto toCreateResponseDto(Rdv rdv) {
        return RdvResponseDto.builder()
                .id(rdv.getId())
                .idPatient(rdv.getIdPatient())
                .idMedecin(rdv.getIdMedecin())
                .dateRdv(rdv.getDateRdv())
                .build();
    }
}
