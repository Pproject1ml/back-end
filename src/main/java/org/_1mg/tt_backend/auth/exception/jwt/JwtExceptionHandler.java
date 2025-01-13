package org._1mg.tt_backend.auth.exception.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.security.core.AuthenticationException;

//인증 도중에 발생하는 예외 처리 (JWT 토큰 관련 예외)
public interface JwtExceptionHandler {

    boolean support(AuthenticationException exception, HttpServletRequest request);

    void handle(HttpServletRequest req, HttpServletResponse resp, ResponseDTO<String> responseDto, AuthenticationException exception);
}
