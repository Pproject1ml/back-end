package org._1mg.tt_backend.chat.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.exception.member.custom.ProfileNotFoundException;
import org._1mg.tt_backend.chat.service.SocketService;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice(assignableTypes = SocketService.class)
public class SocketExceptionHandler {

    @MessageExceptionHandler(ProfileNotFoundException.class)
    public void handleNicknameAlreadyExists(ProfileNotFoundException e) {

        log.error(e.getMessage());

    }
}
