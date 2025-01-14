package com.example.multilab.Controllers;

import com.example.multilab.DTO.MissionRequest;
import com.example.multilab.DTO.MissionResponse;
import com.example.multilab.Entities.Mission;
import com.example.multilab.Entities.ObjetMission;
import com.example.multilab.Entities.ObjetPredifini;
import com.example.multilab.Entities.User;
import com.example.multilab.Repositories.MissionRepo;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import com.example.multilab.Repositories.UserRepo;
import com.example.multilab.Services.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "*")
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        Mission mission = new Mission();
        mission.setOrganisme(missionRequest.getOrganisme());
        mission.setDate(Date.valueOf(missionRequest.getDate()).toLocalDate());
        mission.setUser(user);

        // Create and associate ObjetMission entities
        List<ObjetMission> objetMissions = new ArrayList<>();
        for (Integer objetId : missionRequest.getObjets()) {
            ObjetPredifini objetPredifini = objetPredifiniRepo.findById(objetId)
                    .orElseThrow(() -> new RuntimeException("ObjetPredifini not found"));

            ObjetMission objetMission = new ObjetMission();
            objetMission.setNom(objetPredifini.getNom());
            objetMission.setObjetPredifini(objetPredifini);
            objetMission.setMission(mission); // Associate with the mission
            objetMissions.add(objetMission);
        }
        mission.setObjets(objetMissions);

        // Save the mission (cascade will save ObjetMissions)
        missionRepo.save(mission);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mission added successfully.");
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<Mission>> getAllMissions() {
        List<Mission> missions = missionRepo.findAll();
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<Mission>> getMissionsByUser(@RequestParam("userId") int userId) {
        List<Mission> missions = missionRepo.findByUserId(userId);
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MissionResponse>> getMissionsByDateAndUser(@RequestParam String date, @RequestParam int userId) {
        List<Mission> missions = missionRepo.findAllByDateAndUserId(LocalDate.parse(date), userId);
        List<MissionResponse> response = missions.stream()
                .map(MissionResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMission(@PathVariable int id) {
        boolean isDeleted = missionService.deleteMissionById(id);

        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Mission deleted successfully.");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Mission not found or could not be deleted.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

}
