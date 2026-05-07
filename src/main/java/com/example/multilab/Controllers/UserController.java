package com.example.multilab.Controllers;

import com.example.multilab.Entities.User;
import com.example.multilab.Repositories.UserRepo;
import com.example.multilab.Security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════
 * UserController — Refonte Sécurité
 *
 * Changements :
 * - Login : compare avec BCrypt au lieu de findByUsernameAndPwd
 * - Login : renvoie un token JWT dans la réponse
 * - AddUser : hash le mot de passe avec BCrypt avant sauvegarde
 * - Update : hash le nouveau mot de passe si fourni
 * - @CrossOrigin supprimé (géré par SecurityConfig)
 * ═══════════════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ─────────────────────────────────────────────
    // LOGIN — BCrypt + JWT
    // ─────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // Chercher par username uniquement
        return userRepo.findByUsername(loginRequest.getUsername())
                .map(foundUser -> {
                    // Comparer le mot de passe avec BCrypt
                    if (!passwordEncoder.matches(loginRequest.getPwd(), foundUser.getPwd())) {
                        return ResponseEntity.status(401)
                            .body(Collections.singletonMap("message", "Identifiants incorrects"));
                    }

                    // Générer le token JWT
                    String token = jwtUtil.generateToken(
                        foundUser.getId(),
                        foundUser.getUsername(),
                        foundUser.getRole()
                    );

                    // Réponse avec token + infos user
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Login successful");
                    response.put("token", token);
                    response.put("id", foundUser.getId());
                    response.put("username", foundUser.getUsername());
                    response.put("nom", foundUser.getNom());
                    response.put("prenom", foundUser.getPrenom());
                    response.put("role", foundUser.getRole());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401)
                    .body(Collections.singletonMap("message", "Identifiants incorrects")));
    }

    // ─────────────────────────────────────────────
    // ADD USER — Hash le mot de passe avec BCrypt
    // ─────────────────────────────────────────────
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        // Vérifier si le username existe déjà
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("message", "Ce nom d'utilisateur existe déjà"));
        }

        // Valider les champs requis
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("message", "Le nom d'utilisateur est requis"));
        }
        if (user.getPwd() == null || user.getPwd().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("message", "Le mot de passe est requis"));
        }

        // ─── Hacher le mot de passe ───
        user.setPwd(passwordEncoder.encode(user.getPwd()));

        userRepo.save(user);
        return ResponseEntity.ok(Collections.singletonMap("message", "Utilisateur ajouté avec succès"));
    }

    // ─────────────────────────────────────────────
    // GET ALL USERS
    // ─────────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepo.findAll();
        // Ne pas renvoyer les mots de passe
        users.forEach(u -> u.setPwd(null));
        return ResponseEntity.ok(users);
    }

    // ─────────────────────────────────────────────
    // UPDATE USER — Hash le nouveau pwd si fourni
    // ─────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        return userRepo.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setNom(updatedUser.getNom());
                    user.setPrenom(updatedUser.getPrenom());
                    user.setRole(updatedUser.getRole());

                    // Si un nouveau mot de passe est fourni, le hacher
                    if (updatedUser.getPwd() != null && !updatedUser.getPwd().trim().isEmpty()) {
                        user.setPwd(passwordEncoder.encode(updatedUser.getPwd()));
                    }

                    userRepo.save(user);
                    return ResponseEntity.ok()
                        .body(Collections.singletonMap("message", "Utilisateur mis à jour"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ─────────────────────────────────────────────
    // DELETE USER
    // ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepo.deleteById(id);
        return ResponseEntity.ok()
            .body(Collections.singletonMap("message", "Utilisateur supprimé"));
    }
}
