package com.ehei.auth.DTOS.medecin;


import com.ehei.auth.enums.Role;
import com.ehei.auth.enums.Specialite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedecinResponseDto {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private LocalDate dateNaissance;
    private Role role;
    private Long staffId;
    private Specialite specialite;

}
