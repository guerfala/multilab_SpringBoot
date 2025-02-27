package com.example.multilab.Controllers;

import com.example.multilab.Entities.Kilometrage;
import com.example.multilab.Services.KilometrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/kilometers")
@CrossOrigin(origins = "*")
public class KilometrageController {

    @Autowired
    private KilometrageService kilometrageService;

    @PostMapping("/start")
    public ResponseEntity<String> submitStartKilometers(
            @RequestParam int userId,
            @RequestParam float kilometers) {

        try {
            LocalDate currentDate = LocalDate.now(); // Automatically get current date
            kilometrageService.submitStartKilometers(userId, kilometers, currentDate);
            return ResponseEntity.ok("Kilométrage enregistré avec succès pour la date d'aujourd'hui!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/byUserAndDate")
    public ResponseEntity<Float> getKilometrageByUserAndDate(
            @RequestParam int userId,
            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Optional<Kilometrage> kilometrage = kilometrageService.getKilometrageByUserAndDate(userId, localDate);

            return kilometrage.map(value -> ResponseEntity.ok(value.getKilometrage()))
                    .orElseGet(() -> ResponseEntity.ok(0f)); // Return 0 if no entry found
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
