package com.ehei.staff.models;

import com.ehei.staff.enums.Specialite;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Medecin {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Specialite specialite;

    @OneToOne
    @JoinColumn(name= "staff_id")
    private Staff staff;

}
