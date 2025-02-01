package org._1mg.tt_backend.FCM.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FcmSender {

    public void sendNotification(List<String> tokens, String title, String body) {

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        ApiFuture<BatchResponse> responseFuture = FirebaseMessaging.getInstance().sendEachForMulticastAsync(message);

        try {
            BatchResponse response = responseFuture.get();
            log.info("SEND SUCCESS {} ", response.getSuccessCount());
            log.error("SEND FAILURE {} ", response.getFailureCount());
            log.info("{}", response.getResponses());
        } catch (InterruptedException | ExecutionException e) {
            log.error("ERROR AT SEND NOTIFICATION {} ", e.getMessage());
        }
    }
}
