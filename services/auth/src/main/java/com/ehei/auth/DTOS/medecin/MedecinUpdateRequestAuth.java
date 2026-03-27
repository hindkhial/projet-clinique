package com.ehei.auth.DTOS.medecin;

import com.ehei.auth.enums.Specialite;
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
public class MedecinUpdateRequestAuth {

    @NotBlank(message = "nom obligatoire")
    @Size(min = 2, max = 50, message = "nom doit avoir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "prenom obligatoire")
    @Size(min = 2, max = 50, message = "prenom doit avoir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank
    @Pattern(regexp = "^0[1-9]\\d{8}$",
            message = "Le numéro de téléphone doit être correcte")
    private String tel;

    @NotBlank(message = "Email is required")
    @Email(message = "l'email doit être valid")
    private String email;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @NotNull(message = "La spécialité est obligatoire")
    private Specialite specialite;

    // Optionnel - seulement si changement de mot de passe
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;
}
