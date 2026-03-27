package com.ehei.rdv.DTO.rdv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RdvResponseDto {

    private Long id;

    private Long idPatient;

    private Long idMedecin;

    private LocalDateTime dateRdv;
}
