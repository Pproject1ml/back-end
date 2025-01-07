package org._1mg.tt_backend.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("WebSocket disconnected: sessionId = {}", StompHeaderAccessor.wrap(event.getMessage()).getSessionId());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        if (headerAccessor.getUser() == null) {
            log.error("WebSocket connection failed: no user information found.");
            return;
        }

        log.info("WebSocket connected: sessionId={}, user={}", headerAccessor.getSessionId(), headerAccessor.getUser().getName());

        //이거 경로 지정안해주면 No DefaultDestination configured 에러 남
        //messagingTemplate.convertAndSend("");
    }
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectEvent event) {
//        MessageHeaders headers = event.getMessage().getHeaders();
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        log.info("accessor {}", accessor);
//
//        String token = accessor.getFirstNativeHeader("Authorization");
//        log.info("token {}", token);
//        System.out.println("Token from headers: " + token);
//
//
//        // 추가적인 인증 로직 수행
//    }

}
