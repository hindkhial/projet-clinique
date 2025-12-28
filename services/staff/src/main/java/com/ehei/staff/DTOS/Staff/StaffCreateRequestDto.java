package com.ehei.staff.DTOS.Staff;
import com.ehei.staff.Validators.ValidStaffRole;
import com.ehei.staff.enums.Role;
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
public class StaffCreateRequestDto {

    @NotBlank(message ="nom obligatoire")
    @Size(min = 2, max = 50, message = "nom doit avoir entre 2 et 50 characteres")
    private String nom;

    @NotBlank(message ="prenom obligatoire")
    @Size(min = 2, max = 50, message = "prenom doit avoir entre 2 et 50 characteres")
    private String prenom;

    @NotBlank
    @Pattern(regexp = "^0[1-9]\\d{8}$",
            message = "Le numéro de téléphone doit etre correcte")
    private String tel;

    @NotBlank(message = "Email is required")
    @Email(message = "l'email doit etre valid")
    private String email;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @NotNull(message = "role obligatoire")
    @ValidStaffRole
    private Role role;
}
