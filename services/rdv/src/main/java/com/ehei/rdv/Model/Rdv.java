package com.ehei.rdv.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rdv {
    @Id
    @GeneratedValue
    private Long id;

    private Long idPatient;

    private Long idMedecin;

    private LocalDateTime dateRdv;

}
