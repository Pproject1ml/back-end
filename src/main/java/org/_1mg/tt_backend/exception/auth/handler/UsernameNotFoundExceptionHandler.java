package org._1mg.tt_backend.exception.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static org._1mg.tt_backend.exception.CustomException.NEED_SIGN_UP;

@Component
public class UsernameNotFoundExceptionHandler implements AuthenticationExceptionHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, ResponseDTO<String> responseDto, AuthenticationException exception) {

        responseDto.setStatus(NEED_SIGN_UP.getStatus());
        responseDto.setMessage(NEED_SIGN_UP.getMessage());

        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        resp.setHeader("Location", "/auth/signup");
    }

    @Override
    public boolean support(AuthenticationException exception) {
        return exception.getClass().equals(UsernameNotFoundException.class);
    }
}
