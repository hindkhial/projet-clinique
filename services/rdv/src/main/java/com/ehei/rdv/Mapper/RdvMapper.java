package com.ehei.rdv.Mapper;

import com.ehei.rdv.DTO.RdvCreateRequestDto;
import com.ehei.rdv.DTO.RdvResponseDto;
import com.ehei.rdv.DTO.RdvUpdateRequestDto;
import com.ehei.rdv.Model.Rdv;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
@Service
public class RdvMapper {
    public Rdv toModel(RdvCreateRequestDto requestDto) {
        return Rdv.builder()
                    .idPatient(requestDto.getIdPatient())
                    .idMedecin(requestDto.getIdMedecin())
                    .dateRdv(requestDto.getDateRdv())
                    .build();
    }


   public Rdv toModel(RdvUpdateRequestDto requestDto) {
        return Rdv.builder()
                .idPatient(requestDto.getIdPatient())
                .idMedecin(requestDto.getIdMedecin())
                .dateRdv(requestDto.getDateRdv())
                .build();
    }

    public RdvResponseDto toResponseDto(Rdv rdv) {
        return RdvResponseDto.builder()
                .id(rdv.getId())
                .idPatient(rdv.getIdPatient())
                .idMedecin(rdv.getIdMedecin())
                .dateRdv(rdv.getDateRdv())
                .build();
    }

    public List<RdvResponseDto> toListeResponseDto(List<Rdv> rdvListe) {
        List<RdvResponseDto> toList = new ArrayList<>();
        for (Rdv rdv : rdvListe) {
            toList.add(toResponseDto(rdv));
        }
        return toList;
    }
}
