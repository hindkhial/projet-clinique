package com.ehei.auth.DTOS.patient;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientAccountResult {

    private PatientResponse patient;
    private String keycloakId;
    private boolean success;

    public static PatientAccountResult success(PatientResponse patient, String keycloakId) {
        return PatientAccountResult.builder()
                .patient(patient)
                .keycloakId(keycloakId)
                .success(true)
                .build();
    }
}