package com.ehei.auth.DTOS.patient;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientUpdateRequeste {

    @NotBlank(message = "nom obligatoire")
    private String nom;

    @NotBlank(message = "prenom obligatoire")
    private String prenom;

    @NotBlank
    @Pattern(regexp = "^0[1-9]\\d{8}$",
            message = "Le numéro de téléphone doit etre correcte")
    private String tel;

    @Email
    private String email;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;
}