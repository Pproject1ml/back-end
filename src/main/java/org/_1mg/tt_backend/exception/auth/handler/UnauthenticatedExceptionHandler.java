package org._1mg.tt_backend.exception.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.security.core.AuthenticationException;

public interface UnauthenticatedExceptionHandler {
    boolean support(AuthenticationException exception, HttpServletRequest request);
    void handle(HttpServletRequest req, HttpServletResponse resp, ResponseDTO<String> responseDto, AuthenticationException exception) ;
}
