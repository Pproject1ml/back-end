package org._1mg.tt_backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org._1mg.tt_backend.base.CustomException.UNAUTHORIZED;

/**
 * 인증되었지만 권한이 없는 요청에 대한 예외 처리
 * 인증 완료 후 부여된 권한을 확인
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    //권한 없음 메시지 생성
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ResponseDTO.<String>builder()
                                .status(UNAUTHORIZED.getStatus())
                                .message("YOU HAVE NO ACCESS")
                                .build()
                )
        );
    }
}
