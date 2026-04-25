package com.ehei.rapport.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "rapports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private Long medecinId;

    @Column(nullable = false)
    private String typeRapport;

    @Column(nullable = false)
    private LocalDateTime dateRapport;

    // Données Patient (dénormalisées)
    private String patientNom;
    private String patientPrenom;
    private String patientEmail;

    // Données Médecin (dénormalisées)
    private String medecinNom;
    private String medecinPrenom;
    private String medecinSpecialite;

    // Contenu du rapport
    private String typeMaladie;
    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(columnDefinition = "TEXT")
    private String traitement;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    // Référence à la consultation source
    private Long consultationId;

    // URL du fichier PDF dans MinIO
    private String pdfFileUrl;

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