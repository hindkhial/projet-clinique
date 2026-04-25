package com.ehei.rapport.repository;

import com.ehei.rapport.model.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RapportRepository extends JpaRepository<Rapport, Long> {


    //lister les rapports d un patient
    List<Rapport> findByPatientId(Long patientId);

    //lister les rapports d un medecin
    List<Rapport> findByMedecinId(Long medecinId);

    List<Rapport> findByConsultationId(Long consultationId);
}
