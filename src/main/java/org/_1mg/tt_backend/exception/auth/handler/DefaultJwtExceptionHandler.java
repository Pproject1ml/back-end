package org._1mg.tt_backend.exception.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.exception.auth.CustomJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import static org._1mg.tt_backend.exception.CustomException.*;

@Component
public class DefaultJwtExceptionHandler implements UnauthenticatedExceptionHandler{

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, ResponseDTO<String> responseDto, AuthenticationException exception) {

        responseDto.setStatus(DEFAULT_TOKEN_ERROR.getStatus());
        responseDto.setMessage("ACCESS TOKEN ERROR");

        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    //null 확인안하면 JWT Token 없어도 되는 요청에 대해서 예외 발생함
    @Override
    public boolean support(AuthenticationException exception, HttpServletRequest request) {

        if(request.getAttribute("customException") == null){
            return false;
        }
        return request.getAttribute("customException").getClass().equals(CustomJwtException.class);
    }
}
