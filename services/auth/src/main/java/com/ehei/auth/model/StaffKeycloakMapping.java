package com.ehei.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "staff_keycloak_mapping")
@Data
public class StaffKeycloakMapping implements KeycloakMappable{

    @Id
    @GeneratedValue()
    private Long id;

    @Column(unique = true, nullable = false)
    private Long staffId;  // ID du staff dans le service Staff

    @Column(unique = true, nullable = false)
    private String keycloakUserId;  // ID de l'utilisateur dans Keycloak

    @Column(nullable = false)
    private String email;  // Pour retrouver facilement

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
