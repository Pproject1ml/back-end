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

import static org._1mg.tt_backend.base.CustomException.*;

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

        log.info("WebSocket connected: sessionId={}\n user={}", headerAccessor.getSessionId(), headerAccessor.getUser().getName());
    }

    //Subscribe 이벤트 발생 시 로그
    @EventListener
    public void handleSubscribeListener(SessionSubscribeEvent event) {

        log.info("subscribe event : {}", event.toString());

        //소켓 메세지 헤더 조회
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        MessageType messageType = getMessageType(headerAccessor);

        //APP 시작할 땐 기존에 참여 중이던 채팅방에 다시 구독해야 하기 때문에 아무런 messageType이 없음
        if (messageType == null) {
            return;
        }

        String profileId = getProfileId(headerAccessor);
        String destination = getDestination(headerAccessor);
        String chatroomId = getChatroomId(destination);

        switch (messageType) {
            case JOIN -> {
                log.info("SUBSCRIBE JOIN");
                List<TextDTO> joinMessages = socketService.makeWelcomeMessage(profileId, chatroomId);
                sendMessage(joinMessages, destination);
                log.info("SUBSCRIBE JOIN END");
            }
            case ENTER -> {
                log.info("SUBSCRIBE ENTER");
                log.info("SUBSCRIBE ENTER END");
            }
        }
    }

    //Unsubscribe 이벤트 발생 시 로그
    @EventListener
    public void handleUnsubscribeListener(SessionUnsubscribeEvent event) {

        log.info("unsubscribe event : {}", event.toString());

        //소켓 메세지 헤더 조회
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        MessageType messageType = getMessageType(headerAccessor);
        if (messageType == null) {
            return;
        }

        String profileId = getProfileId(headerAccessor);
        //구독 해제에선 destination을 보낼 수 없음 그래서 일단 직접 받도록 했는데 어떻게 조회할 수 있을 것도 같은데..
        String chatroomId = headerAccessor.getFirstNativeHeader("chatroomId");
        //그래서 일단 직접 만들어야 함
        String destination = "/sub/chat/" + chatroomId;

        switch (messageType) {
            case LEAVE -> {
                log.info("UNSUBSCRIBE LEAVE");
                log.info("UNSUBSCRIBE LEAVE END");
            }
            case DISABLE -> {
                log.info("UNSUBSCRIBE DISABLE");
                List<TextDTO> result = socketService.makeDisableMessage(profileId, chatroomId);
                sendMessage(result, destination);
                log.info("UNSUBSCRIBE DISABLE END");
            }
            case DIE -> {
                log.info("UNSUBSCRIBE DIE");
                List<TextDTO> result = socketService.makeDieMessage(profileId, chatroomId);
                sendMessage(result, destination);
                log.info("UNSUBSCRIBE DIE END");
            }
        }
    }

    MessageType getMessageType(StompHeaderAccessor headerAccessor) {

        //COMMAND로 메세지 타입을 전달받음
        String command = headerAccessor.getFirstNativeHeader("COMMAND");
        if (command == null) {
            log.error("Subscribe COMMAND IS NULL");
            return null;
        }

        MessageType messageType = MessageType.getMessageType(command.toUpperCase());
        if (messageType == null) {
            log.error("{}", INVALID_MESSAGE_TYPE.getMessage() + " : " + command);
            throw new IllegalArgumentException(INVALID_MESSAGE_TYPE.getMessage() + " " + command);
        }

        return messageType;
    }

    String getProfileId(StompHeaderAccessor headerAccessor) {
        return headerAccessor.getFirstNativeHeader("profileId");
    }

    String getDestination(StompHeaderAccessor headerAccessor) {

        //destination은 구독하는 URL을 의미함
        String destination = headerAccessor.getFirstNativeHeader("destination");
        if (destination == null) {
            log.error("{}", DESTINATION_IS_NULL.getMessage() + " : " + headerAccessor.getSessionId());
            throw new IllegalArgumentException(DESTINATION_IS_NULL.getMessage());
        }

        return destination;
    }

    String getChatroomId(String destination) {
        String[] parts = destination.split("/");
        return parts[parts.length - 1];
    }

    void sendMessage(List<TextDTO> messages, String destination) {

        //시간 메세지와 입장/퇴장 메세지 전송
        for (TextDTO message : messages) {
            messagingTemplate.convertAndSend(destination,
                    ResponseDTO.<TextDTO>builder()
                            .status(OK.getStatus())
                            .message(OK.getMessage())
                            .data(message)
                            .build());
        }
    }
}
