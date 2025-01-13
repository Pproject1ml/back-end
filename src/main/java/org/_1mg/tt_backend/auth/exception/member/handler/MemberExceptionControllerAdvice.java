package org._1mg.tt_backend.auth.exception.member.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.controller.MemberController;
import org._1mg.tt_backend.auth.exception.member.NicknameAlreadyExistsException;
import org._1mg.tt_backend.auth.exception.member.UserAlreadyExistsException;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static org._1mg.tt_backend.base.CustomException.ALREADY_EXISTS_NICKNAME;
import static org._1mg.tt_backend.base.CustomException.ALREADY_EXISTS_USER;

@Slf4j
@RestControllerAdvice(assignableTypes = MemberController.class)
@RequiredArgsConstructor
public class MemberExceptionControllerAdvice {

    private final ObjectMapper objectMapper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseDTO<String> userAlreadyExist(UserAlreadyExistsException exception) {

        return ResponseDTO.<String>builder()
                .status(ALREADY_EXISTS_USER.getStatus())
                .message(ALREADY_EXISTS_USER.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NicknameAlreadyExistsException.class)
    public ResponseDTO<String> nicknameAlreadyException(NicknameAlreadyExistsException exception) throws JsonProcessingException {

        return ResponseDTO.<String>builder()
                .status(ALREADY_EXISTS_NICKNAME.getStatus())
                .message(ALREADY_EXISTS_NICKNAME.getMessage())
                .data(objectMapper.writeValueAsString(Map.of("nickname", exception.getInvalidNickname())))
                .build();
    }
}
