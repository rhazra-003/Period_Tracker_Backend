package com.project.periodtracker.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            String credentialsPath = System.getenv().getOrDefault("FIREBASE_CREDENTIALS_PATH",
                    System.getenv().getOrDefault("GOOGLE_APPLICATION_CREDENTIALS", "src/main/resources/firebase-service-account.json"));

            Path resolvedPath = Path.of(credentialsPath);
            if (!Files.exists(resolvedPath)) {
                throw new IOException("Firebase credentials file not found at: " + credentialsPath);
            }

            try (FileInputStream serviceAccount = new FileInputStream(resolvedPath.toFile())) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        }
    }
} 