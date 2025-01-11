package com.example.multilab.Controllers;

import com.example.multilab.DTO.ObjetMissionDTO;
import com.example.multilab.DTO.ObjetPredifiniDTO;
import com.example.multilab.DTO.OrdreDTO;
import com.example.multilab.Entities.*;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import com.example.multilab.Repositories.OrdreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ordres")
@CrossOrigin(origins = "*")
public class OrdreController {

    @Autowired
    private OrdreRepo ordreRepo;

    @Autowired
    private ObjetPredifiniRepo objetPredifiniRepo;

    @PostMapping
    public ResponseEntity<OrdreDTO> createOrdre(@RequestBody Ordre ordre) {
        // Create a new Ordre
        Ordre newOrdre = new Ordre();
        newOrdre.setOrganisme(ordre.getOrganisme());
        newOrdre.setDateDebut(LocalDateTime.now());
        newOrdre.setStatus(Status.NONREALISE);

        // Convert objetsIds into ObjetMission entities
        if (ordre.getObjetsIds() != null) {
            for (Integer objetId : ordre.getObjetsIds()) {
                ObjetPredifini objetPredifini = objetPredifiniRepo.findById(objetId)
                        .orElseThrow(() -> new RuntimeException("ObjetPredifini not found with ID: " + objetId));

                ObjetMission objetMission = new ObjetMission();
                objetMission.setNom(objetPredifini.getNom());
                objetMission.setObjetPredifini(objetPredifini);
                objetMission.setCause("");
                objetMission.setEtat(Etat.NONFINI);
                objetMission.setOrdre(newOrdre);

                newOrdre.getObjets().add(objetMission);
            }
        }

        Ordre savedOrdre = ordreRepo.save(newOrdre);

        // Convert Ordre to OrdreDTO
        OrdreDTO ordreDTO = new OrdreDTO();
        ordreDTO.setId(savedOrdre.getId());
        ordreDTO.setOrganisme(savedOrdre.getOrganisme());

        List<ObjetMissionDTO> missionDTOs = savedOrdre.getObjets().stream().map(mission -> {
            ObjetMissionDTO missionDTO = new ObjetMissionDTO();
            missionDTO.setId(mission.getId());

            ObjetPredifiniDTO predifiniDTO = new ObjetPredifiniDTO();
            predifiniDTO.setId(mission.getObjetPredifini().getId());
            predifiniDTO.setNom(mission.getObjetPredifini().getNom());

            missionDTO.setObjetPredifini(predifiniDTO);
            return missionDTO;
        }).toList();

        ordreDTO.setObjetMissions(missionDTOs);

        return ResponseEntity.ok(ordreDTO);
    }


    @GetMapping
    public List<Ordre> getAllOrdres() {
        return ordreRepo.findAll();
    }
}
