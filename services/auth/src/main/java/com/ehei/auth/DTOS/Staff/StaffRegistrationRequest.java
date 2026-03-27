package com.ehei.auth.DTOS.Staff;

import com.ehei.auth.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StaffRegistrationRequest {
    
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;
    
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;
    
    private String tel;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @NotNull(message = "role obligatoire")
    private Role role;
}