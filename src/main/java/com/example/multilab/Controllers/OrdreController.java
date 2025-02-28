package com.example.multilab.Controllers;

import com.example.multilab.DTO.*;
import com.example.multilab.Entities.*;
import com.example.multilab.Repositories.*;
import com.example.multilab.Services.OrdreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordres")
@CrossOrigin(origins = "*")
public class OrdreController {

    @Autowired
    private OrdreRepo ordreRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MissionRepo missionRepo;

    @Autowired
    private ObjetMissionRepo objetMissionRepo;

    @PostMapping("/create")
    public ResponseEntity<String> createOrdre(@RequestBody OrdreAddDTO ordreAddDTO) {
        Mission mission = missionRepo.findById(ordreAddDTO.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        User user = userRepo.findById(ordreAddDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ordre ordre = new Ordre();
        ordre.setOrganisme(ordreAddDTO.getOrganisme());
        ordre.setMission(mission);
        ordre.setUser(user);
        ordre.setStatus(Status.ENCOURS);
        ordre.setDateDebut(LocalDateTime.now());

        ordreRepo.save(ordre);

        // 🔹 Link all objetMissions to this ordre
        List<ObjetMission> objets = mission.getObjets();
        for (ObjetMission objetMission : objets) {
            objetMission.setOrdre(ordre); // Link to ordre
        }
        objetMissionRepo.saveAll(objets); // Save changes

        return ResponseEntity.ok("Ordre created and linked with Mission successfully.");
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
    public ResponseEntity<?> settleOrdre(@PathVariable int ordreId, @RequestBody List<ObjetMissionUpdateDTO> updatedMissions) {
        Ordre ordre = ordreRepo.findById(ordreId)
                .orElseThrow(() -> new RuntimeException("Ordre not found"));

        // Update each ObjetMission based on the DTO
        for (ObjetMissionUpdateDTO dto : updatedMissions) {
            ObjetMission existingMission = ordre.getObjets().stream()
                    .filter(m -> m.getId() == dto.getId())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("ObjetMission not found"));
            existingMission.setEtat(dto.getEtat());
            existingMission.setCause(dto.getCause());
        }

        // Check if all missions are ticked.
        // Assuming that the ticked state is represented by Etat.FINI.
        boolean allTicked = updatedMissions.stream().allMatch(dto -> dto.getEtat() == Etat.FINIS);
        if (allTicked) {
            ordre.setStatus(Status.REALISE);
        } else {
            ordre.setStatus(Status.NONREALISE);
        }

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

            if (ordre.getDateFin() == null)
            {
                ordreDTO.setDateFin("N/A");
            }else {
                ordreDTO.setDateFin(ordre.getDateFin().toString());
            }

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

    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Long>> getOrderStatsForUser(@PathVariable int userId) {
        List<Object[]> stats = ordreRepo.getOrderStatsByUser(userId);
        Map<String, Long> result = new HashMap<>();

        // Initialize counts to zero
        result.put("REALISE", 0L);
        result.put("NONREALISE", 0L);
        result.put("ENCOURS", 0L);

        // Update with counts from query result
        for (Object[] stat : stats) {
            Status status = (Status) stat[0];
            Long count = (Long) stat[1];
            result.put(status.name(), count);
        }

        return ResponseEntity.ok(result);
    }
}
