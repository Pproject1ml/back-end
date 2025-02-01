package org._1mg.tt_backend.chat.exception.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.chat.exception.custom.ChatroomNotFoundException;
import org._1mg.tt_backend.chat.exception.custom.ProfileNotParticipants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org._1mg.tt_backend.base.CustomException.CHATROOM_NOT_FOUND;
import static org._1mg.tt_backend.base.CustomException.USER_NOT_IN_CHATROOM;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ChatUtilsExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatroomNotFoundException.class)
    public ResponseDTO<String> chatroomNotFound(ChatroomNotFoundException e) {

        log.error("CHATROOM NOT FOUND");
        return ResponseDTO.<String>builder()
                .status(CHATROOM_NOT_FOUND.getStatus())
                .message(CHATROOM_NOT_FOUND.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProfileNotParticipants.class)
    public ResponseDTO<String> profileNotParticipants(ProfileNotParticipants e) {

        log.error("USER NOT IN CHATROOM");
        return ResponseDTO.<String>builder()
                .status(USER_NOT_IN_CHATROOM.getStatus())
                .message(USER_NOT_IN_CHATROOM.getMessage())
                .build();
    }

}
