package com.ehei.analyses.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}