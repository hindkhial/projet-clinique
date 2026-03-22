package com.ehei.patient.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientCreatedEvent {

    // Métadonnées de l'événement
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

    // Données du patient
    private Long patientId;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private LocalDate dateNaissance;

    // Constructeur utilitaire pour créer l'événement facilement
    public static PatientCreatedEvent from(Long patientId, String nom, String prenom,
                                           String email, String tel, LocalDate dateNaissance) {
        return PatientCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PatientCreated")
                .timestamp(LocalDateTime.now())
                .patientId(patientId)
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .tel(tel)
                .dateNaissance(dateNaissance)
                .build();
    }
}