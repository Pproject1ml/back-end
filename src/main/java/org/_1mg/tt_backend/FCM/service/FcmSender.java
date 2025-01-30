package org._1mg.tt_backend.FCM.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FcmSender {

    public void sendNotification(List<String> tokens, String title, String body) {

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            log.info("SUCCESS SENT response: {}", response.getSuccessCount());
            log.error("FAILURE SENT response: {}", response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
        }
    }
}
