package com.example.multilab.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Enumerated(EnumType.STRING)
    private Etat Etat;

    private String Cause;

    @ManyToOne
    @JsonIgnore
    private Ordre ordre;

    @ManyToOne
    private ObjetPredifini objetPredifini;

}
