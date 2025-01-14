package org._1mg.tt_backend.auth.exception.jwt.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._1mg.tt_backend.auth.exception.jwt.custom.JwtInvalidSignException;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import static org._1mg.tt_backend.base.CustomException.SIGNATURE_INVALID;

@Component
public class JwtSignExceptionHandler implements JwtExceptionHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, ResponseDTO<String> responseDto, AuthenticationException exception) {

        responseDto.setStatus(SIGNATURE_INVALID.getStatus());
        responseDto.setMessage(SIGNATURE_INVALID.getMessage());

        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Override
    public boolean support(AuthenticationException exception, HttpServletRequest request) {

        if (request.getAttribute("customException") == null) {
            return false;
        }
        return request.getAttribute("customException") instanceof JwtInvalidSignException;
    }
}
