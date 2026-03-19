package com.ehei.rdv.Repository;

import com.ehei.rdv.Model.Rdv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;


public interface RdvRepository extends JpaRepository<Rdv,Long> {
    boolean existsByIdMedecinAndDateRdv(Long id, LocalDateTime rdv);
}
