package com.ehei.auth.DTOS.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private LocalDate dateNaissance;
}