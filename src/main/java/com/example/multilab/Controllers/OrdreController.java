package com.example.multilab.Controllers;

import com.example.multilab.DTO.*;
import com.example.multilab.Entities.*;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import com.example.multilab.Repositories.OrdreRepo;
import com.example.multilab.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/ordres")
@CrossOrigin(origins = "*")
public class OrdreController {

    @Autowired
    private OrdreRepo ordreRepo;

    @Autowired
    private ObjetPredifiniRepo objetPredifiniRepo;

    @Autowired
    private UserRepo userRepo;

    @PostMapping
    public ResponseEntity<OrdreAddDTO> createOrdre(@RequestBody Ordre ordre, @RequestHeader("user-id") int userId) {
        // Find the user who is making the order
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a new Ordre
        Ordre newOrdre = new Ordre();
        newOrdre.setOrganisme(ordre.getOrganisme());
        newOrdre.setDateDebut(LocalDateTime.now());
        newOrdre.setStatus(Status.ENCOURS);
        newOrdre.setUser(user);

        // Add associated ObjetMission entities
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

        // Return a DTO response
        OrdreAddDTO ordreDTO = new OrdreAddDTO();
        ordreDTO.setId(savedOrdre.getId());
        ordreDTO.setOrganisme(savedOrdre.getOrganisme());

        return ResponseEntity.ok(ordreDTO);
    }

    @GetMapping("/by-day")
    public ResponseEntity<List<OrdreDTO>> getOrdersByDay(
            @RequestParam("date") String dateString,
            @RequestParam("userId") int userId) {
        // Parse the date string into LocalDate
        LocalDate date = LocalDate.parse(dateString); // Handles "2025-01-11" correctly

        // Convert LocalDate to LocalDateTime range
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<OrdreDTO> ordreDTOs = ordreRepo.findByUserIdAndDateDebutBetween(userId, startOfDay, endOfDay).stream().map(ordre -> {
            OrdreDTO ordreDTO = new OrdreDTO();
            ordreDTO.setId(ordre.getId());
            ordreDTO.setOrganisme(ordre.getOrganisme());
            ordreDTO.setStatus(ordre.getStatus().toString());
            ordreDTO.setDateDebut(ordre.getDateDebut().toString());

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

    @PostMapping("/{ordreId}/settle")
    public ResponseEntity<?> settleOrdre(@PathVariable int ordreId, @RequestBody List<ObjetMission> updatedMissions) {
        Ordre ordre = ordreRepo.findById(ordreId)
                .orElseThrow(() -> new RuntimeException("Ordre not found"));

        // Update ObjetMissions
        for (ObjetMission updatedMission : updatedMissions) {
            ObjetMission existingMission = ordre.getObjets().stream()
                    .filter(mission -> mission.getId() == updatedMission.getId())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("ObjetMission not found"));

            existingMission.setEtat(updatedMission.getEtat());
            existingMission.setCause(updatedMission.getCause());
        }

        // Update the ordre status to REALISE
        ordre.setStatus(Status.REALISE);
        ordre.setDateFin(LocalDateTime.now());
        ordreRepo.save(ordre);

        return ResponseEntity.ok("Ordre settled successfully");
    }

    @GetMapping("/{ordreId}/missions")
    public ResponseEntity<List<ObjetMission>> getObjetMissions(@PathVariable int ordreId) {
        Ordre ordre = ordreRepo.findById(ordreId)
                .orElseThrow(() -> new RuntimeException("Ordre not found"));

        return ResponseEntity.ok(ordre.getObjets());
    }

    @GetMapping("/admin/orders-by-day")
    public ResponseEntity<List<OrdreAdminDTO>> getOrdersByDayForAdmin(
            @RequestParam("date") String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<OrdreAdminDTO> ordreDTOs = ordreRepo.findByDateDebutBetween(startOfDay, endOfDay).stream().map(ordre -> {
            OrdreAdminDTO ordreDTO = new OrdreAdminDTO();
            ordreDTO.setId(ordre.getId());
            ordreDTO.setOrganisme(ordre.getOrganisme());
            ordreDTO.setStatus(ordre.getStatus().toString());
            ordreDTO.setDateDebut(ordre.getDateDebut().toString());
            ordreDTO.setUsername(ordre.getUser().getUsername()); // Include user name

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



    /*@GetMapping("/getordresbyuser")
    public ResponseEntity<List<OrdreDTO>> getOrdresByUser(@RequestHeader("user-id") int userId) {
        List<OrdreDTO> ordreDTOs = ordreRepo.findByUserId(userId).stream().map(ordre -> {
            OrdreDTO ordreDTO = new OrdreDTO();
            ordreDTO.setId(ordre.getId());
            ordreDTO.setOrganisme(ordre.getOrganisme());
            ordreDTO.setStatus(ordre.getStatus().toString());
            ordreDTO.setDateDebut(ordre.getDateDebut().toString());

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

    @GetMapping("/getall")
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
    }*/
}
