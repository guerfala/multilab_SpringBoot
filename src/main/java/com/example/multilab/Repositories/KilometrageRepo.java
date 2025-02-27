package com.example.multilab.Repositories;

import com.example.multilab.Entities.Kilometrage;
import com.example.multilab.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface KilometrageRepo extends JpaRepository<Kilometrage, Integer> {
    Optional<Kilometrage> findByUserAndDate(User user, LocalDate date);
}
