package com.example.multilab.Services;

import com.example.multilab.Entities.*;
import com.example.multilab.Repositories.MissionRepo;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import com.example.multilab.Repositories.OrdreRepo;
import com.example.multilab.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrdreService {

    @Autowired
    private OrdreRepo ordreRepository;

    @Autowired
    private MissionRepo missionRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ObjetPredifiniRepo objetPredifiniRepo;

    public Ordre updateOrdre(int id, Ordre ordreDetails) {
        return ordreRepository.findById(id).map(ordre -> {
            ordre.setDateDebut(ordreDetails.getDateDebut());
            ordre.setDateFin(ordreDetails.getDateFin());
            ordre.setOrganisme(ordreDetails.getOrganisme());
            ordre.setStatus(ordreDetails.getStatus());
            return ordreRepository.save(ordre);
        }).orElseThrow(() -> new RuntimeException("Ordre non trouvé"));
    }
}
