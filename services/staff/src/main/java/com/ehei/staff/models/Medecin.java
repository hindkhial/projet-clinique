package com.ehei.staff.models;

import com.ehei.staff.enums.Specialite;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Medecin {
    @Id
    @GeneratedValue
    private Long id;
    private Specialite specialite;

    @OneToOne
    @JoinColumn(name= "staff_id")
    private Staff staff;

}
