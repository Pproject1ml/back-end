package org._1mg.tt_backend.FCM.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    private final String firebasePath;

    public FirebaseConfig(@Value("${firebase.admin.path}") String path) {
        this.firebasePath = path;
    }

    @PostConstruct
    public void initialize() {

        log.info("Initializing Firebase");
        try {
            InputStream serviceAccount = new FileInputStream(firebasePath);
            log.info("finding file {}", firebasePath);

            byte[] streamBytes = StreamUtils.copyToByteArray(serviceAccount);
            log.info("service account {}", new String(streamBytes, StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(streamBytes)))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}