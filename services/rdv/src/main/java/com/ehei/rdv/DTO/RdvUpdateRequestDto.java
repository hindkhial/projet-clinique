package com.ehei.rdv.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RdvUpdateRequestDto {
    @NotNull(message = "patient obligatoire")
    @NotBlank(message = "patient obligatoire")
    private Long idPatient;

    @NotNull(message = "medecin obligatoire")
    @NotBlank(message = "medecin obligatoire")
    private Long idMedecin;

    @Future(message = "la date doit etre au future")
    @NotNull(message = "date obligatoire")
    @NotBlank(message = "date obligatoire")
    private LocalDateTime dateRdv;

}

