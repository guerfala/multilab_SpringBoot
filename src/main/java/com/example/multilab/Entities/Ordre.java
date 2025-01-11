package com.example.multilab.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ordre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime DateDebut;

    private LocalDateTime DateFin;

    private String Organisme;

    @ElementCollection
    private List<Integer> objetsIds;

    @OneToMany(mappedBy = "ordre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObjetMission> Objets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    @AssertTrue(message = "La date de début doit être antérieure à la date de fin")
    public boolean isDatesValid() {
        return DateDebut.isBefore(DateFin);
    }
}
