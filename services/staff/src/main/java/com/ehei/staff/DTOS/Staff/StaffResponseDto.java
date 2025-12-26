package com.ehei.staff.DTOS.Staff;

import com.ehei.staff.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffResponseDto {
    private Long id;
    private String nom;
    private String prenom;
    private String tel;
    private Role role;
}
