package com.ehei.auth.repository;

import com.ehei.auth.model.PatientKeycloakMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientKeycloakMappingRepository extends JpaRepository<PatientKeycloakMapping, Long> {
    Optional<PatientKeycloakMapping> findByPatientId(Long patientId);
    void deleteByPatientId(Long patientId);
}