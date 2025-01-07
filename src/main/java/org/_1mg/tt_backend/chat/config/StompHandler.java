package org._1mg.tt_backend.chat.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.jwt.JwtUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final JwtUtils jwtUtil;

    // websocket을 통해 들어온 요청이 처리되기 전 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // websocket 연결 시 헤더의 jwt token 유효성 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String authorization = accessor.getFirstNativeHeader("Authorization");
            if (authorization == null || authorization.isEmpty()) {
                log.error("JWT Token IS NULL_Handler");
                throw new IllegalArgumentException("Authorization header is missing or empty");
            }

            String token = authorization.substring(7); // "Bearer " 제거

            try {
                Claims claims = jwtUtil.verifyToken(token); // 토큰 검증 및 클레임 추출
                String memberId = jwtUtil.getSubject(claims);
                String role = jwtUtil.getRole(claims);
                log.info("WebSocket connection authorized for memberId: {}, role: {}", memberId, role);
            } catch (Exception e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                throw new IllegalArgumentException("Invalid JWT token", e);
            }
        }

        return message;
    }
}
