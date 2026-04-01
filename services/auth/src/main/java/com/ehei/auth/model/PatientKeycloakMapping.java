package com.ehei.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_keycloak_mapping")
@Data
public class PatientKeycloakMapping implements KeycloakMappable{

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private Long patientId;

    @Column(unique = true, nullable = false)
    private String keycloakUserId;

    @Column(nullable = false)
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}