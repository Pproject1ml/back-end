package org._1mg.tt_backend.chat.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.exception.custom.AlreadyInChatroomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org._1mg.tt_backend.base.CustomException.USER_ALREADY_IN_CHATROOM;

@Slf4j
@RestControllerAdvice
public class ChatroomExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyInChatroomException.class)
    public ResponseDTO<String> alreadyInChatroom(AlreadyInChatroomException exception) {

        log.error("ALREADY IN CHATROOM");
        return ResponseDTO.<String>builder()
                .status(USER_ALREADY_IN_CHATROOM.getStatus())
                .message(USER_ALREADY_IN_CHATROOM.getMessage())
                .build();
    }
}
