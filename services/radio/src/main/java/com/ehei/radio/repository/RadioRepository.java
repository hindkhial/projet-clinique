package com.ehei.radio.repository;

import com.ehei.radio.model.Radio;

import java.util.List;

public interface RadioRepository {
    // Find all radios for a patient
    List<Radio> findByPatientId(Long patientId);

    // Find all radios by a radiologue
    List<Radio> findByRadiologueId(Long radiologueId);

    // Check if radio exists
    boolean existsById(Long id);
}
