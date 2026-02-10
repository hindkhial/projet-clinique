package com.ehei.radio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadioResponse {
    private Long id;
    private Long patientId;
    private String patientNom;
    private String patientPrenom;
    private Long radiologueId;
    private LocalDateTime dateRadio;
    private String fichierRadioUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

