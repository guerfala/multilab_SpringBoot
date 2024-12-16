package com.example.multilab.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false)
    private String Username;

    @Column(length = 50, nullable = false)
    private String Pwd;

    @Column(length = 50, nullable = false)
    private String Nom;

    @Column(length = 50, nullable = false)
    private String Prenom;
}
