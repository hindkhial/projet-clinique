package com.ehei.patient.model;

import com.ehei.patient.config.CryptoConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Patient {

    @Id
    @GeneratedValue
    private Long id;
    @Convert(converter = CryptoConverter.class)
    private String nom;

    @Convert(converter = CryptoConverter.class)
    private String prenom;

    @Convert(converter = CryptoConverter.class)
    private String email;

    @Convert(converter = CryptoConverter.class)
    private String tel;

    private LocalDate dateNaissance;
}