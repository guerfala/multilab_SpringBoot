package com.example.multilab.Controllers;

import com.example.multilab.DTO.MissionRequest;
import com.example.multilab.DTO.MissionResponse;
import com.example.multilab.Entities.*;
import com.example.multilab.Exception.ResourceNotFoundException;
import com.example.multilab.Repositories.MissionRepo;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import com.example.multilab.Repositories.UserRepo;
import com.example.multilab.Services.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════════════
 * MissionController — Refonte Backend
 *
 * Changements :
 * - ResourceNotFoundException au lieu de RuntimeException
 * - @CrossOrigin supprimé (géré par SecurityConfig)
 * - Réponses structurées
 * ═══════════════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/missions")
public class MissionController {

    @Autowired
    private MissionRepo missionRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ObjetPredifiniRepo objetPredifiniRepo;

    @Autowired
    private MissionService missionService;

    @PostMapping
    public ResponseEntity<Map<String, String>> addMission(@RequestBody MissionRequest missionRequest) {
        User user = userRepo.findById(missionRequest.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Utilisateur", "id", missionRequest.getUser().getId()));

        Mission mission = new Mission();
        mission.setOrganisme(missionRequest.getOrganisme());
        mission.setDate(Date.valueOf(missionRequest.getDate()).toLocalDate());
        mission.setUser(user);

        List<ObjetMission> objetMissions = new ArrayList<>();
        for (Integer objetPredifiniId : missionRequest.getObjets()) {
            ObjetPredifini objetPredifini = objetPredifiniRepo.findById(objetPredifiniId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "ObjetPredifini", "id", objetPredifiniId));

            ObjetMission objetMission = new ObjetMission();
            objetMission.setObjetPredifini(objetPredifini);
            objetMission.setMission(mission);
            objetMission.setNom(objetPredifini.getNom());
            objetMissions.add(objetMission);
        }

        mission.setObjets(objetMissions);
        missionRepo.save(mission);

        return ResponseEntity.ok(Map.of("message", "Mission ajoutée avec succès"));
    }

    @GetMapping
    public ResponseEntity<List<Mission>> getAllMissions() {
        return ResponseEntity.ok(missionRepo.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MissionResponse>> getMissionsByUser(@PathVariable int userId) {
        // Vérifier que l'utilisateur existe
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur", "id", userId);
        }

        List<Mission> missions = missionService.getMissionsByUser(userId);
        List<MissionResponse> response = missions.stream()
                .map(MissionResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MissionResponse>> getMissionsByDateAndUser(
            @RequestParam String date, @RequestParam int userId) {

        List<Mission> missions = missionRepo.findAllByDateAndUserId(LocalDate.parse(date), userId);
        List<MissionResponse> response = missions.stream()
                .map(MissionResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMission(@PathVariable int id) {
        if (!missionRepo.existsById(id)) {
            throw new ResourceNotFoundException("Mission", "id", id);
        }

        missionService.deleteMissionById(id);
        return ResponseEntity.ok(Map.of("message", "Mission supprimée avec succès"));
    }
}
