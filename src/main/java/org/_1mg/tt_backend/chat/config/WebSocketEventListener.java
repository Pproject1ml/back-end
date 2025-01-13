package org._1mg.tt_backend.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.chat.MessageType;
import org._1mg.tt_backend.chat.dto.DieDTO;
import org._1mg.tt_backend.chat.dto.JoinDTO;
import org._1mg.tt_backend.chat.dto.LeaveDTO;
import org._1mg.tt_backend.chat.service.ChatService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

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

    /**
     * SUBSCRIBE 헤더 처리
     * ENTER - 채팅방에 입장
     * JOIN - 채팅방 가입
     * 없음 - Chatroom List 구독, 채팅방 퇴장 시 재구독
     */
    @EventListener
    public void handleSubscribeListener(SessionSubscribeEvent event) {
        log.info("subscribe event : {}", event.toString());

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String value = headerAccessor.getFirstNativeHeader("COMMAND");
        if (value == null) {
            log.info("Subscribe COMMAND IS NULL");
            return;
        }

        MessageType messageType = MessageType.getMessageType(value.toUpperCase());
        if (messageType == null) {
            throw new RuntimeException("Unsupported message type: " + value);
        }

        switch (messageType) {
            case ENTER -> {
                log.info("Subscribe ENTER");
            }
            case JOIN -> {
                log.info("Subscribe JOIN");
                String profileId = headerAccessor.getFirstNativeHeader("profileId");
                String chatroomId = headerAccessor.getFirstNativeHeader("chatroomId");
                String nickname = headerAccessor.getFirstNativeHeader("nickname");
                JoinDTO joinDTO = JoinDTO.builder()
                        .profileId(profileId)
                        .chatroomId(chatroomId)
                        .build();

                chatService.joinChatroom(joinDTO);
                String joinMessage = nickname + "님이 입장하셨습니다";
                messagingTemplate.convertAndSend("/sub/room/" + chatroomId, joinMessage);
            }
            default -> {
                log.info("Subscribe UNKNOWN");
            }
        }
    }

    /**
     * UNSUBSCRIBE 헤더 처리
     * LEAVE - 채팅방 퇴장
     * DIE - 채팅방 탈퇴
     * 업음 - 채팅방 입장 전에 잠깐 끊기
     */
    @EventListener
    public void handleUnsubscribeListener(SessionUnsubscribeEvent event) {
        log.info("unsubscribe event : {}", event.toString());

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String value = headerAccessor.getFirstNativeHeader("COMMAND");
        if (value == null) {
            log.info("Unsubscribe COMMAND IS NULL");
            return;
        }

        MessageType messageType = MessageType.getMessageType(value.toUpperCase());
        if (messageType == null) {
            throw new RuntimeException("Unsupported message type: " + value);
        }

        switch (messageType) {
            case LEAVE -> {
                log.info("UnSubscribe LEAVE");
                String profileId = headerAccessor.getFirstNativeHeader("profileId");
                String chatroomId = headerAccessor.getFirstNativeHeader("chatroomId");
                LeaveDTO leaveDTO = LeaveDTO.builder()
                        .profileId(profileId)
                        .chatroomId(chatroomId)
                        .build();

                chatService.leaveChatroom(leaveDTO);
            }
            case DIE -> {
                log.info("UnSubscribe DIE");
                String profileId = headerAccessor.getFirstNativeHeader("profileId");
                String chatroomId = headerAccessor.getFirstNativeHeader("chatroomId");
                DieDTO dieDTO = DieDTO.builder()
                        .profileId(profileId)
                        .chatroomId(chatroomId)
                        .build();

                chatService.dieChatroom(dieDTO);
            }
            default -> {
                log.info("UnSubscribe UNKNOWN");
            }
        }
    }

    @EventListener
    public void handleSendEvent(GenericMessage<?> message) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        log.info("================EVENT================");
        log.info("header Accessor = {}", headerAccessor.getCommand());
        log.info("header Accessor = {}", headerAccessor.getSessionId());
        log.info("header Accessor = {}", headerAccessor.getUser());
        log.info("payload = {}", message.getPayload());
    }
}
