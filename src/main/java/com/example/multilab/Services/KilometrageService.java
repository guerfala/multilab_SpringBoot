package com.example.multilab.Services;

import com.example.multilab.Entities.Kilometrage;
import com.example.multilab.Entities.User;
import com.example.multilab.Repositories.KilometrageRepo;
import com.example.multilab.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class KilometrageService {

    @Autowired
    private KilometrageRepo kilometrageRepository;

    @Autowired
    private UserRepo userRepository;

    public Kilometrage submitStartKilometers(int userId, float kilometrage, LocalDate date) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Utilisateur introuvable avec l'ID: " + userId);
        }

        User user = userOptional.get();
        Optional<Kilometrage> existingKilometrage = kilometrageRepository.findByUserAndDate(user, date);

        if (existingKilometrage.isPresent()) {
            // Update existing record
            Kilometrage kilometrageEntry = existingKilometrage.get();
            kilometrageEntry.setKilometrage(kilometrage);
            return kilometrageRepository.save(kilometrageEntry);
        } else {
            // Create new record
            Kilometrage newKilometrageEntry = new Kilometrage();
            newKilometrageEntry.setUser(user);
            newKilometrageEntry.setDate(date);
            newKilometrageEntry.setKilometrage(kilometrage);
            return kilometrageRepository.save(newKilometrageEntry);
        }
    }

    public Optional<Kilometrage> getKilometrageByUserAndDate(int userId, LocalDate date) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        return kilometrageRepository.findByUserAndDate(userOptional.get(), date);
    }

}
