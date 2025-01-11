package com.example.multilab.Controllers;

import com.example.multilab.DTO.ObjetMissionDTO;
import com.example.multilab.DTO.ObjetPredifiniDTO;
import com.example.multilab.DTO.OrdreAddDTO;
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
    public ResponseEntity<OrdreAddDTO> createOrdre(@RequestBody Ordre ordre) {
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
        OrdreAddDTO ordreDTO = new OrdreAddDTO();
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
    public ResponseEntity<List<OrdreDTO>> getAllOrdres() {
        List<OrdreDTO> ordreDTOs = ordreRepo.findAll().stream().map(ordre -> {
            OrdreDTO ordreDTO = new OrdreDTO();
            ordreDTO.setId(ordre.getId());
            ordreDTO.setOrganisme(ordre.getOrganisme());
            ordreDTO.setStatus(ordre.getStatus().toString()); // Map status
            ordreDTO.setDateDebut(ordre.getDateDebut().toString()); // Map dateDebut

            List<ObjetMissionDTO> objetMissionDTOs = ordre.getObjets().stream().map(objet -> {
                ObjetMissionDTO objetMissionDTO = new ObjetMissionDTO();
                objetMissionDTO.setId(objet.getId());
                objetMissionDTO.setDescription(objet.getCause());

                ObjetPredifiniDTO predifiniDTO = new ObjetPredifiniDTO();
                predifiniDTO.setId(objet.getObjetPredifini().getId());
                predifiniDTO.setNom(objet.getObjetPredifini().getNom());

                objetMissionDTO.setObjetPredifini(predifiniDTO);
                return objetMissionDTO;
            }).toList();

            ordreDTO.setObjetMissions(objetMissionDTOs);
            return ordreDTO;
        }).toList();

        return ResponseEntity.ok(ordreDTOs);
    }
}
