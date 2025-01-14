package org._1mg.tt_backend.auth.exception.member.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.controller.MemberController;
import org._1mg.tt_backend.auth.exception.member.custom.NicknameAlreadyExistsException;
import org._1mg.tt_backend.auth.exception.member.custom.UserAlreadyExistsException;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org._1mg.tt_backend.base.CustomException.*;

@Slf4j
@RestControllerAdvice(assignableTypes = MemberController.class)
@RequiredArgsConstructor
public class MemberExceptionControllerAdvice {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseDTO<String> userAlreadyExists(UserAlreadyExistsException exception) {

        log.error("USER ALREADY EXISTS");
        return ResponseDTO.<String>builder()
                .status(ALREADY_EXISTS_USER.getStatus())
                .message(ALREADY_EXISTS_USER.getMessage())
                .build();
    }

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
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDTO<String> refreshTokenIsNull(IllegalArgumentException exception) {

        log.error("REFRESH TOKEN IS NULL");
        return ResponseDTO.<String>builder()
                .status(REFRESH_TOKEN_IS_NULL.getStatus())
                .message(REFRESH_TOKEN_IS_NULL.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseDTO<String> jwtExpired(ExpiredJwtException exception) {

        log.error("REFRESH TOKEN EXPIRED");
        return ResponseDTO.<String>builder()
                .status(EXPIRED_TOKEN.getStatus())
                .message(EXPIRED_TOKEN.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SignatureException.class)
    public ResponseDTO<String> jwtSignatureInvalid(SignatureException exception) {

        log.error("SIGNATURE IS INVALID");
        return ResponseDTO.<String>builder()
                .status(SIGNATURE_INVALID.getStatus())
                .message(SIGNATURE_INVALID.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(JwtException.class)
    public ResponseDTO<String> jwtError(JwtException exception) {

        log.error("REFRESH TOKEN ERROR");
        return ResponseDTO.<String>builder()
                .status(DEFAULT_TOKEN_ERROR.getStatus())
                .message(DEFAULT_TOKEN_ERROR.getMessage())
                .build();
    }
}
