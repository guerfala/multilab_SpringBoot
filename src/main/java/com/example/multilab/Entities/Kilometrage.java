package com.example.multilab.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date"})} // Ensure unique user+date
)
public class Kilometrage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private float kilometrage;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
