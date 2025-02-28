package com.example.multilab.Repositories;

import com.example.multilab.Entities.Ordre;
import com.example.multilab.Entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface OrdreRepo extends JpaRepository<Ordre, Integer> {
    List<Ordre> findByUserId(int userId);

    List<Ordre> findByUserIdAndDateDebutBetween(int userId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Ordre> findByDateDebutBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    boolean existsByMissionIdAndStatus(int missionId, Status status);
    @Query("SELECT o.status, COUNT(o) FROM Ordre o WHERE o.user.id = :userId GROUP BY o.status")
    List<Object[]> getOrderStatsByUser(@Param("userId") int userId);
}
