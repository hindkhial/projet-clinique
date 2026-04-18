package com.ehei.radio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRadioRequest {

    @NotNull(message = "Patient ID est obligatoire")
    private Long patientId;

    @NotNull(message = "Radiologue ID est obligatoire")
    private Long radiologueId;

    @NotNull(message = "Date est obligatoire")
    private LocalDateTime dateRadio;

    private String notes;
}

