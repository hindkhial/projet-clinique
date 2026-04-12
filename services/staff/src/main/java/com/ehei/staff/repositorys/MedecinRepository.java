package com.ehei.staff.repositorys;


import com.ehei.staff.models.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    Optional<Medecin> findByStaff_Id(Long staffId);
}
