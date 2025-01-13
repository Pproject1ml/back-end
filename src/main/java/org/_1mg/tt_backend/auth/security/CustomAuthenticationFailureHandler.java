package org._1mg.tt_backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.exception.auth.handler.AuthenticationFailureExceptionHandler;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.DEFAULT_AUTH_ERROR;

/**
 * 인증 실패했을 때 발생하는 예외 처리
 * 인증이 끝난 후 실패한 인증에 대한 예외를 잡음
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final List<AuthenticationFailureExceptionHandler> handlers;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        //인증 실패에 대한 기본 응답 메시지
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        responseDTO.setStatus(DEFAULT_AUTH_ERROR.getStatus());
        responseDTO.setMessage(exception.getMessage());

        //특별히 처리해야 할 예외
        //현재 등록되어 있는 Handler : UsernameNotFoundExceptionHandler
        for (AuthenticationFailureExceptionHandler handler : handlers) {
            if (handler.support(exception)) {
                handler.handle(request, response, responseDTO, exception);
                break;
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(
                objectMapper.writeValueAsString(responseDTO)
        );
    }
}
