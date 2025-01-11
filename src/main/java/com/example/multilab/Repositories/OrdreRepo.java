package com.example.multilab.Repositories;

import com.example.multilab.Entities.Ordre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface OrdreRepo extends JpaRepository<Ordre, Integer> {
    List<Ordre> findByUserId(int userId);

    List<Ordre> findByUserIdAndDateDebutBetween(int userId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Ordre> findByDateDebutBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
