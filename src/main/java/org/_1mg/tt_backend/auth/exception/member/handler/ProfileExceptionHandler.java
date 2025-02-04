package org._1mg.tt_backend.auth.exception.member.handler;

import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.exception.member.custom.NicknameAlreadyExistsException;
import org._1mg.tt_backend.auth.exception.member.custom.ProfileNotFoundException;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org._1mg.tt_backend.base.CustomException.ALREADY_EXISTS_NICKNAME;
import static org._1mg.tt_backend.base.CustomException.USER_NOT_FOUND;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfileExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NicknameAlreadyExistsException.class)
    public ResponseDTO<String> nicknameAlreadyExists(NicknameAlreadyExistsException exception) {

        log.error("NOT UNIQUE NICKNAME");
        return ResponseDTO.<String>builder()
                .status(ALREADY_EXISTS_NICKNAME.getStatus())
                .message(ALREADY_EXISTS_NICKNAME.getMessage())
                .data(exception.getInvalidNickname())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseDTO<String> profileNotFound(ProfileNotFoundException exception) {

        log.error("PROFILE NOT FOUND");
        return ResponseDTO.<String>builder()
                .status(USER_NOT_FOUND.getStatus())
                .message(USER_NOT_FOUND.getMessage())
                .build();
    }
}
