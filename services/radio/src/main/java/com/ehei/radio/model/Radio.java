package com.ehei.radio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "radios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Radio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long radiologueId;

    @Column(nullable = false)
    private LocalDateTime dateRadio;

    // URL du fichier radio dans MinIO
    @Column(nullable = false)
    private String fichierRadioUrl;

    // Informations patient (dénormalisées)
    private String patientNom;
    private String patientPrenom;

    // Notes optionnelles
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

