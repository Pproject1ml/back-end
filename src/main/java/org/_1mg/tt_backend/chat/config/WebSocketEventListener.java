package org._1mg.tt_backend.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.TextDTO;
import org._1mg.tt_backend.chat.service.SocketService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;

import static org._1mg.tt_backend.base.CustomException.OK;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final SocketService socketService;

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
    }

    //Subscribe 이벤트 발생 시 로그
    @EventListener
    public void handleSubscribeListener(SessionSubscribeEvent event) {
        log.info("subscribe event : {}", event.toString());

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String command = headerAccessor.getFirstNativeHeader("COMMAND");
        if (command == null) {
            log.info("Subscribe COMMAND IS NULL");
            return;
        }

        MessageType messageType = MessageType.getMessageType(command.toUpperCase());
        if (messageType == null) {
            throw new RuntimeException("Unsupported message type: " + command);
        }

        switch (messageType) {
            case JOIN -> {
                log.info("SUBSCRIBE JOIN");

                String profileId = headerAccessor.getFirstNativeHeader("profileId");
                String destination = headerAccessor.getFirstNativeHeader("destination");
                if (destination == null) {
                    throw new IllegalArgumentException("Destination header is missing");
                }

                String[] parts = destination.split("/");
                String chatroomId = parts[parts.length - 1];
                List<TextDTO> joinMessages = socketService.makeWelcomeMessage(profileId, chatroomId);

                if (joinMessages.size() > 1) {
                    messagingTemplate.convertAndSend(destination,
                            ResponseDTO.<TextDTO>builder()
                                    .status(OK.getStatus())
                                    .message(OK.getMessage())
                                    .data(joinMessages.get(0))
                                    .build());
                }

                messagingTemplate.convertAndSend(destination,
                        ResponseDTO.<TextDTO>builder()
                                .status(OK.getStatus())
                                .message(OK.getMessage())
                                .data(joinMessages.get(joinMessages.size() - 1))
                                .build());
            }
            default -> {
                log.info("ERROR");
            }
        }
    }

    //Unsubscribe 이벤트 발생 시 로그
    @EventListener
    public void handleUnsubscribeListener(SessionUnsubscribeEvent event) {
        log.info("unsubscribe event : {}", event.toString());
    }
}
