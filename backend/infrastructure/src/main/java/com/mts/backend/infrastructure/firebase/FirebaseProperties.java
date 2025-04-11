package com.mts.backend.infrastructure.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "firebase-config")
@Data
public class FirebaseProperties {
    private String bucket;
    private FirebaseApp firebaseApp;

    @PostConstruct
    public void initialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("account-key.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .setStorageBucket(bucket)
                .build();
        
        if (FirebaseApp.getApps().isEmpty()) {
            this.firebaseApp = FirebaseApp.initializeApp(options);
        }
    }
    
    
}
