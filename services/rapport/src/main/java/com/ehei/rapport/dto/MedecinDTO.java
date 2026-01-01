package com.ehei.rapport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String specialite;
    private String tel;
    private String cin;
    private Long userId;
}