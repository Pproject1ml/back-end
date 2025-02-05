package org._1mg.tt_backend.chat.exception.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.exception.member.custom.ProfileNotFoundException;
import org._1mg.tt_backend.chat.exception.custom.AlreadyInChatroomException;
import org._1mg.tt_backend.chat.exception.custom.ProfileNotParticipants;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SocketExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final String destination = "/sub/room/";

    @MessageExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException ex, Principal principal) {

        log.error(ex.getMessage());
    }

    @MessageExceptionHandler(ProfileNotFoundException.class)
    public void handleNicknameAlreadyExists(ProfileNotFoundException e) {

        log.error(e.getMessage());
    }

    @MessageExceptionHandler(AlreadyInChatroomException.class)
    public void handleAlreadyInChatroom(AlreadyInChatroomException e) {

        log.error(e.getMessage());
    }

    @MessageExceptionHandler(ProfileNotParticipants.class)
    public void handleProfileNotParticipants(ProfileNotParticipants e) {

        log.error(e.getMessage());
    }
}