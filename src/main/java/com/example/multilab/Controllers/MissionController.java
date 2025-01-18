package com.example.multilab.Controllers;

import com.example.multilab.DTO.MissionRequest;
import com.example.multilab.DTO.MissionResponse;
import com.example.multilab.Entities.*;
import com.example.multilab.Repositories.MissionRepo;
import com.example.multilab.Repositories.ObjetPredifiniRepo;
import com.example.multilab.Repositories.UserFCMTokenRepo;
import com.example.multilab.Repositories.UserRepo;
import com.example.multilab.Services.FCMService;
import com.example.multilab.Services.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
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

    @Autowired
    private FCMService fcmService;

    @Autowired
    private UserFCMTokenRepo userFCMTokenRepo;

    @PostMapping
    public ResponseEntity<Map<String, String>> addMission(@RequestBody MissionRequest missionRequest) {
        // ✅ Get assigned user
        User user = userRepo.findById(missionRequest.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Retrieve the correct FCM token from UserFCMToken entity
        Optional<UserFCMToken> userFCMTokenOptional = userFCMTokenRepo.findByUser(user);
        if (userFCMTokenOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User's FCM Token not found!"));
        }

        String fcmToken = userFCMTokenOptional.get().getFcmToken();

        // ✅ Create Mission
        Mission mission = new Mission();
        mission.setOrganisme(missionRequest.getOrganisme());
        mission.setDate(Date.valueOf(missionRequest.getDate()).toLocalDate());
        mission.setUser(user);

        missionRepo.save(mission);

        // ✅ Send Firebase Notification
        try {
            String notificationResponse = fcmService.sendNotification(fcmToken, "New Mission Assigned", "You have a new mission!");
            System.out.println(notificationResponse);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(Map.of("message", "Mission added successfully."));
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
