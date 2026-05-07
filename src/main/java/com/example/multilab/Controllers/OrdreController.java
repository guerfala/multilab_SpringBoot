package com.example.multilab.Controllers;

import com.example.multilab.DTO.*;
import com.example.multilab.Entities.*;
import com.example.multilab.Exception.ResourceNotFoundException;
import com.example.multilab.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════
 * OrdreController — Refonte Backend
 *
 * Changements :
 * - ResourceNotFoundException au lieu de RuntimeException
 * - @CrossOrigin supprimé (géré par SecurityConfig)
 * - Réponses d'erreur structurées
 * - Logique de mapping DTO inchangée (à migrer vers Service
 *   dans une prochaine itération)
 * ═══════════════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/ordres")
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
    public ResponseEntity<Map<String, String>> createOrdre(@RequestBody OrdreAddDTO ordreAddDTO) {
        Mission mission = missionRepo.findById(ordreAddDTO.getMissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", ordreAddDTO.getMissionId()));

        User user = userRepo.findById(ordreAddDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", ordreAddDTO.getUserId()));

        Ordre ordre = new Ordre();
        ordre.setOrganisme(ordreAddDTO.getOrganisme());
        ordre.setMission(mission);
        ordre.setUser(user);
        ordre.setStatus(Status.ENCOURS);
        ordre.setDateDebut(LocalDateTime.now());

        ordreRepo.save(ordre);

        // Lier les objetMissions à cet ordre
        List<ObjetMission> objets = mission.getObjets();
        for (ObjetMission objetMission : objets) {
            objetMission.setOrdre(ordre);
        }
        objetMissionRepo.saveAll(objets);

        return ResponseEntity.ok(Map.of("message", "Ordre créé et lié à la mission avec succès"));
    }

    @GetMapping("/by-day")
    public ResponseEntity<List<OrdreDTO>> getOrdersByDay(
            @RequestParam("date") String dateString,
            @RequestParam("userId") int userId) {

        LocalDate date = LocalDate.parse(dateString);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<OrdreDTO> ordreDTOs = ordreRepo
            .findByUserIdAndDateDebutBetween(userId, startOfDay, endOfDay)
            .stream()
            .map(this::toOrdreDTO)
            .toList();

        return ResponseEntity.ok(ordreDTOs);
    }

    @PostMapping("/{ordreId}/settle")
    public ResponseEntity<Map<String, String>> settleOrdre(
            @PathVariable int ordreId,
            @RequestBody List<ObjetMissionUpdateDTO> updatedMissions) {

        Ordre ordre = ordreRepo.findById(ordreId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre", "id", ordreId));

        for (ObjetMissionUpdateDTO dto : updatedMissions) {
            ObjetMission existingMission = ordre.getObjets().stream()
                    .filter(m -> m.getId() == dto.getId())
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("ObjetMission", "id", dto.getId()));
            existingMission.setEtat(dto.getEtat());
            existingMission.setCause(dto.getCause());
        }

        boolean allCompleted = updatedMissions.stream()
            .allMatch(dto -> dto.getEtat() == Etat.FINIS);

        ordre.setStatus(allCompleted ? Status.REALISE : Status.NONREALISE);
        ordre.setDateFin(LocalDateTime.now());
        ordreRepo.save(ordre);

        String statusMsg = allCompleted ? "RÉALISÉ" : "NON RÉALISÉ";
        return ResponseEntity.ok(Map.of("message", "Ordre réglé — " + statusMsg));
    }

    @GetMapping("/{ordreId}/missions")
    public ResponseEntity<List<ObjetMission>> getObjetMissions(@PathVariable int ordreId) {
        Ordre ordre = ordreRepo.findById(ordreId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre", "id", ordreId));
        return ResponseEntity.ok(ordre.getObjets());
    }

    @GetMapping("/admin/orders-by-day")
    public ResponseEntity<List<OrdreAdminDTO>> getOrdersByDayForAdmin(
            @RequestParam("date") String dateString) {

        LocalDate date = LocalDate.parse(dateString);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<OrdreAdminDTO> ordreDTOs = ordreRepo
            .findByDateDebutBetween(startOfDay, endOfDay)
            .stream()
            .map(this::toOrdreAdminDTO)
            .toList();

        return ResponseEntity.ok(ordreDTOs);
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Long>> getOrderStatsForUser(@PathVariable int userId) {
        // Vérifier que l'utilisateur existe
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur", "id", userId);
        }

        List<Object[]> stats = ordreRepo.getOrderStatsByUser(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("REALISE", 0L);
        result.put("NONREALISE", 0L);
        result.put("ENCOURS", 0L);

        for (Object[] stat : stats) {
            Status status = (Status) stat[0];
            Long count = (Long) stat[1];
            result.put(status.name(), count);
        }

        return ResponseEntity.ok(result);
    }

    // ─── Méthodes de mapping privées ───

    private OrdreDTO toOrdreDTO(Ordre ordre) {
        OrdreDTO dto = new OrdreDTO();
        dto.setId(ordre.getId());
        dto.setOrganisme(ordre.getOrganisme());
        dto.setStatus(ordre.getStatus().toString());
        dto.setDateDebut(ordre.getDateDebut().toString());

        List<ObjetMissionDTO> objetDTOs = ordre.getObjets().stream()
            .map(this::toObjetMissionDTO)
            .toList();
        dto.setObjetMissions(objetDTOs);

        return dto;
    }

    private OrdreAdminDTO toOrdreAdminDTO(Ordre ordre) {
        OrdreAdminDTO dto = new OrdreAdminDTO();
        dto.setId(ordre.getId());
        dto.setOrganisme(ordre.getOrganisme());
        dto.setStatus(ordre.getStatus().toString());
        dto.setDateDebut(ordre.getDateDebut().toString());
        dto.setUsername(ordre.getUser().getUsername());
        dto.setDateFin(ordre.getDateFin() != null ? ordre.getDateFin().toString() : "N/A");

        List<ObjetMissionDTO> objetDTOs = ordre.getObjets().stream()
            .map(this::toObjetMissionDTO)
            .toList();
        dto.setObjetMissions(objetDTOs);

        return dto;
    }

    private ObjetMissionDTO toObjetMissionDTO(ObjetMission objet) {
        ObjetMissionDTO dto = new ObjetMissionDTO();
        dto.setId(objet.getId());
        dto.setDescription(objet.getCause());

        ObjetPredifiniDTO predifiniDTO = new ObjetPredifiniDTO();
        predifiniDTO.setId(objet.getObjetPredifini().getId());
        predifiniDTO.setNom(objet.getObjetPredifini().getNom());
        dto.setObjetPredifini(predifiniDTO);

        return dto;
    }
}
