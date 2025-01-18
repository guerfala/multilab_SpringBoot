package com.example.multilab.Controllers;

import com.example.multilab.Entities.User;
import com.example.multilab.Entities.UserFCMToken;
import com.example.multilab.Repositories.UserFCMTokenRepo;
import com.example.multilab.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/fcm")
public class UserFCMTokenController {

    @Autowired
    private UserFCMTokenRepo userFCMTokenRepo;

    @Autowired
    private UserRepo userRepo;

    // ✅ Update or save FCM Token when user logs in
    @PostMapping("/update-token")
    public ResponseEntity<?> updateFcmToken(@RequestParam int userId, @RequestParam String fcmToken) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();
        Optional<UserFCMToken> existingToken = userFCMTokenRepo.findByUser(user);

        UserFCMToken userFCMToken;
        if (existingToken.isPresent()) {
            userFCMToken = existingToken.get();
            userFCMToken.setFcmToken(fcmToken);
        } else {
            userFCMToken = new UserFCMToken();
            userFCMToken.setUser(user);
            userFCMToken.setFcmToken(fcmToken);
        }

        userFCMTokenRepo.save(userFCMToken);
        return ResponseEntity.ok("FCM Token updated successfully");
    }

    // ✅ Retrieve FCM Token for a user
    @GetMapping("/get-token")
    public ResponseEntity<String> getFcmToken(@RequestParam int userId) {
        Optional<UserFCMToken> tokenOptional = userFCMTokenRepo.findByUserId(userId);
        return tokenOptional.map(userFCMToken -> ResponseEntity.ok(userFCMToken.getFcmToken()))
                .orElseGet(() -> ResponseEntity.badRequest().body("FCM Token not found"));
    }
}
