package org._1mg.tt_backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org._1mg.tt_backend.auth.dto.LoginDTO;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;


/**
 * 로그인을 담당하는 Filter
 * 로그인 시작점임 AuthenticationManager를 통해 AuthenticationProvider를 호출함으로써 인증 수행 
 */
public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/auth/login", "POST");
    private final ObjectMapper objectMapper;

    public CustomLoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("AUTHENTICATION METHOD NOT SUPPORTED : " + request.getMethod());
        }

        LoginDTO input = objectMapper.readValue(request.getInputStream(), LoginDTO.class);

        String oauthId = check(input.getOauthId());
        String email = check(input.getEmail());
        String oauthProvider = check(input.getOauthProvider());

        String profileImage = check(input.getProfileImage());
        String nickname = check(input.getNickname());

        MemberDTO memberDto = MemberDTO.builder()
                .oauthId(oauthId)
                .email(email)
                .oauthProvider(oauthProvider)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
        
        //인증 전 Token의 Principal에는 입력된 memberDTO가 들어감
        CustomAuthenticationToken authRequest = CustomAuthenticationToken.unauthenticated(oauthId, email, oauthProvider, memberDto);
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    //지금은 따로 동작 없음 나중에 요청에 다른 데이터도 들어올 수 있으니까..
    protected void setDetails(HttpServletRequest request, CustomAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private String check(String input){
        return input != null ? input.trim() : null;
    }
}
