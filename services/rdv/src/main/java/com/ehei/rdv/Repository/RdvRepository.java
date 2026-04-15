package com.ehei.rdv.Repository;

import com.ehei.rdv.Model.Rdv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface RdvRepository extends JpaRepository<Rdv,Long> {
    boolean existsByIdMedecinAndDateRdv(Long id, LocalDateTime rdv);
    List<Rdv> findAllByIdPatient(Long idPatient);
    List<Rdv> findAllByIdMedecin(Long idMedecin);
    List<Rdv> findAllByIdMedecinAndDateRdvBetween(Long idMedecin, LocalDateTime start, LocalDateTime end);
}
