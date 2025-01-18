package com.example.multilab.Services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {

    public String sendNotification(String fcmToken, String title, String body) throws ExecutionException, InterruptedException {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("title", title)  // ✅ Ensure the payload contains notification data
                .putData("body", body)
                .build();

        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        System.out.println("✅ FCM Notification Sent: " + response);
        return response;
    }
}
