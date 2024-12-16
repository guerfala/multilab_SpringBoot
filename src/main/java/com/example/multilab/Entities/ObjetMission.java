package com.example.multilab.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjetMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false)
    private String Nom;

    private Etat Etat;

    private String Cause;

    @ManyToOne
    private Ordre ordre;

    @ManyToOne
    private ObjetPredifini objetPredifini;

}
