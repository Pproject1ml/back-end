package org._1mg.tt_backend.auth.exception.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.security.core.AuthenticationException;

//잘못된 인증 정보로 인한 인증 실패 처리
public interface AuthenticationFailureExceptionHandler {

    boolean support(AuthenticationException exception);

    void handle(HttpServletRequest req, HttpServletResponse resp, ResponseDTO<String> responseDto, AuthenticationException exception);
}
