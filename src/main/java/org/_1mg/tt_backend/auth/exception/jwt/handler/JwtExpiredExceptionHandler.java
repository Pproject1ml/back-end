package org._1mg.tt_backend.auth.exception.jwt.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._1mg.tt_backend.auth.exception.jwt.custom.JwtExpiredTokenException;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import static org._1mg.tt_backend.base.CustomException.EXPIRED_TOKEN;

@Component
public class JwtExpiredExceptionHandler implements JwtExceptionHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, ResponseDTO<String> responseDto, AuthenticationException exception) {

        responseDto.setStatus(EXPIRED_TOKEN.getStatus());
        responseDto.setMessage(EXPIRED_TOKEN.getMessage());

        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        resp.setHeader("Location", "/auth/refresh");
    }

    @Override
    public boolean support(AuthenticationException exception, HttpServletRequest request) {

        if (request.getAttribute("customException") == null) {
            return false;
        }
        return request.getAttribute("customException") instanceof JwtExpiredTokenException || exception instanceof JwtExpiredTokenException;
    }
}
