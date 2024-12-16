package com.example.multilab.Repositories;

import com.example.multilab.Entities.Ordre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdreRepo extends JpaRepository<Ordre, Integer> {
}
