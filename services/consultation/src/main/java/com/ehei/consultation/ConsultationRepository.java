package com.ehei.consultation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByIdPatient(Long idPatient);

    List<Consultation> findByIdMedecin(Long idMedecin);
}
