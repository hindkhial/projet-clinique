package com.ehei.auth.DTOS.Staff;


import com.ehei.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffResponseDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private LocalDate dateNaissance;
    private Role role;
}


