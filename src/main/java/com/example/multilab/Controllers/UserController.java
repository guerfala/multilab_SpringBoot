package com.example.multilab.Controllers;

import com.example.multilab.Entities.User;
import com.example.multilab.Repositories.UserRepo;
import com.example.multilab.Services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:4200")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return userRepo.findByUsernameAndPwd(user.getUsername(), user.getPwd())
                .map(foundUser -> {
                    // Build a JSON response with user details
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Login successful");
                    response.put("id", foundUser.getId());
                    response.put("username", foundUser.getUsername());
                    response.put("nom", foundUser.getNom());
                    response.put("prenom", foundUser.getPrenom());
                    response.put("role", foundUser.getRole());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401).body(Collections.singletonMap("message", "Invalid credentials")));
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        userRepo.save(user);
        return ResponseEntity.ok("{\"message\": \"User added successfully\"}");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(users);
    }

}
