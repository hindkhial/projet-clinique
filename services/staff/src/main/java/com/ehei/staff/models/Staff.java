package com.ehei.staff.models;

import com.ehei.staff.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Staff {
    @Id
    @GeneratedValue
    private Long id;
    private String nom;
    private String prenom;
    private String tel;

    @Enumerated(EnumType.STRING)
    private Role role;
}
