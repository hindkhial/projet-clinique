package com.ehei.staff.DTOS.Staff;

import com.ehei.staff.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffCreateRequestDto {

    @NotBlank(message ="nom obligatoire")
    private String nom;

    @NotBlank(message ="prenom obligatoire")
    private String prenom;

    @NotBlank
    private String tel;

    @NotNull
    private Role role;
}
