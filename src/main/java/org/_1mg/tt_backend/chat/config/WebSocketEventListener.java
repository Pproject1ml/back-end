package org._1mg.tt_backend.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("WebSocket disconnected: sessionId = {}", StompHeaderAccessor.wrap(event.getMessage()).getSessionId());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getSessionId() != null) {
            log.info("WebSocket connected: sessionId = {}", headerAccessor.getSessionId());
        } else {
            log.warn("WebSocket connection event received, but no session ID found.");
        }
    }
}
