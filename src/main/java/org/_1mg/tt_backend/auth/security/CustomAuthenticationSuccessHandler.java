package org._1mg.tt_backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org._1mg.tt_backend.auth.jwt.JwtUtils;
import org._1mg.tt_backend.auth.service.MemberService;
import org._1mg.tt_backend.base.ResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 로그인 성공 시 호출된 처리기
 * access token 발급해서 응답 Authorization 헤더에 추가
 * refresh token 발급해서 DB에 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String memberId = userDetails.getMemberId();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Long EXPIRED_ACCESS = 3 * 30 * 24 * 60 * 60 * 1000L;
        //Long EXPIRED_ACCESS = 30 * 1000L;
        String access = jwtUtils.createJwt("access", memberId, roles, EXPIRED_ACCESS);
        log.info("ACCESS TOKEN : {}", access);

        Long EXPIRED_REFRESH = 12 * 30 * 24 * 60 * 60L;
        String refresh = jwtUtils.createJwt("refresh", memberId, roles, EXPIRED_REFRESH);
        memberService.saveRefreshToken(memberId, refresh);

        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Authorization", "Bearer " + access);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResponseDTO<MemberDTO> responseDTO = ResponseDTO.<MemberDTO>builder()
                .status(HttpServletResponse.SC_OK)
                .message("LOGIN SUCCESS")
                .data(new MemberDTO(userDetails.getMember()))
                .build();

        objectMapper.writeValue(response.getWriter(), responseDTO);
    }
}
