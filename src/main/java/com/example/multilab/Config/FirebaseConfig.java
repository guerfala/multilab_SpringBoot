package com.example.multilab.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        String base64Credentials = System.getenv("FIREBASE_CREDENTIALS");
        if (base64Credentials == null || base64Credentials.isEmpty()) {
            throw new IllegalStateException("Missing FIREBASE_CREDENTIALS environment variable");
        }

        byte[] decodedJson = Base64.getDecoder().decode(base64Credentials);
        ByteArrayInputStream credentialsStream = new ByteArrayInputStream(decodedJson);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
