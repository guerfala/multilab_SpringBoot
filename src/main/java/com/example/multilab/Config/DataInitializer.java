package com.example.multilab.Config;

import com.example.multilab.Entities.User;
import com.example.multilab.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ═══════════════════════════════════════════════════════════
 * DataInitializer — Crée le premier admin au démarrage
 *
 * Vérifie si un utilisateur "admin" existe déjà.
 * Si non, en crée un avec le mot de passe hashé en BCrypt.
 *
 * ⚠️ Changer le mot de passe après la première connexion !
 * ⚠️ Supprimer cette classe en production si nécessaire.
 * ═══════════════════════════════════════════════════════════
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Vérifier si l'admin existe déjà
        if (userRepo.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPwd(passwordEncoder.encode("admin123")); // ← BCrypt auto
            admin.setNom("Administrateur");
            admin.setPrenom("Principal");
            admin.setRole("Admin");

            userRepo.save(admin);

            System.out.println("══════════════════════════════════════════");
            System.out.println("  ✅ Admin créé avec succès !");
            System.out.println("  👤 Username : admin");
            System.out.println("  🔑 Password : admin123");
            System.out.println("  ⚠️  Changez ce mot de passe !");
            System.out.println("══════════════════════════════════════════");
        } else {
            System.out.println("  ℹ️  Admin existe déjà, rien à faire.");
        }
    }
}
