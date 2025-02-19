package com.example.multilab.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String nom;

    @Enumerated(EnumType.STRING)
    private Etat etat;

    private String cause;

    @ManyToOne
    @JsonIgnore
    private Ordre ordre;

    @ManyToOne
    @JsonIgnore
    @JsonBackReference
    private Mission mission;

    @ManyToOne
    private ObjetPredifini objetPredifini;

    @Override
    public String toString() {
        return "ObjetMission{" +
                "id=" + id +
                ", cause='" + cause + '\'' +
                ", objetPredifini=" + (objetPredifini != null ? objetPredifini.getNom() : "null") +
                '}'; // 🚫 Removed mission reference to prevent recursion
    }


}
