package com.ehei.radio.repository;

import com.ehei.radio.model.Radio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RadioRepository extends JpaRepository<Radio, Long> {

    // Trouver tous les radios d'un patient
    List<Radio> findByPatientId(Long patientId);

    // Trouver tous les radios d'un radiologue
    List<Radio> findByRadiologueId(Long radiologueId);
}