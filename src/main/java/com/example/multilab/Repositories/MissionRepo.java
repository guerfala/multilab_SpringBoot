package com.example.multilab.Repositories;

import com.example.multilab.Entities.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MissionRepo extends JpaRepository<Mission, Integer> {
    List<Mission> findByUserId(int userId);
    List<Mission> findAllByDateAndUserId(LocalDate date, int userId);

}
