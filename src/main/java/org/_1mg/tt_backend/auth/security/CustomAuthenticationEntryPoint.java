package org._1mg.tt_backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.exception.jwt.handler.JwtExceptionHandler;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static org._1mg.tt_backend.base.CustomException.NEED_SIGN_IN;

/**
 * 인증이 안된 요청에 대한 예외 처리
 * 인증 도중에 발생하는 예외를 잡음
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final List<JwtExceptionHandler> handlers;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        String url = request.getRequestURI();
        //기본적인 예외 응답 메시지 양식
        ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                .status(NEED_SIGN_IN.getStatus())
                .message(url + " " + NEED_SIGN_IN.getMessage())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("Location", "/auth/login");

        //특별히 처리 방법이 다른 예외에 대한 처리
        //현재 등록되어 있는 Handler : JwtExpiredExceptionHandler
        for (JwtExceptionHandler handler : handlers) {
            if (handler.support(authException, request)) {
                handler.handle(request, response, responseDTO, authException);
                break;
            }
        }

        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(responseDTO)
        );
    }
}
