package org._1mg.tt_backend.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 실제 인증 과정이 구현
 * 1. 인증 토큰을 Custom 양식으로 형변환
 * 2. OAuthID 존재 확인
 * 3. OAuthID로 DB에서 사용자 정보 조회
 * 4. 입력과 DB에서 조회된 데이터 비교
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info("Authenticate Start");
        Assert.isInstanceOf(CustomAuthenticationToken.class, authentication, () -> "Only CustomAuthenticationToken is supported");
        CustomAuthenticationToken unauthenticatedToken = (CustomAuthenticationToken) authentication;

        String oauthId = determineUsername(unauthenticatedToken);

        CustomUserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(oauthId);
        } catch (UsernameNotFoundException e) {
            log.error("USER NOT FOUND : {} {}", e.getMessage(), oauthId);
            throw e;
        }

        //구글, kakao 로그인 형식 구분
        try {
            switch (unauthenticatedToken.getOauthProvider()) {
                case "GOOGLE":
                    checkGoogle(userDetails, unauthenticatedToken);
                    break;
                case "KAKAO":
                    checkKakao(userDetails, unauthenticatedToken);
                    break;
            }
        } catch (BadCredentialsException e) {
            log.error(e.getMessage());
            throw e;
        }

        //인증된 토큰 생성
        //주목할 점은 Principal로 UserDetails가 들어감
        CustomAuthenticationToken authenticatedToken = CustomAuthenticationToken.authenticated(
                UUID.fromString(userDetails.getMemberId()),
                userDetails.getEmail(),
                unauthenticatedToken.getOauthProvider(),
                userDetails,
                userDetails.getAuthorities()
        );

        log.info("Authenticate End");
        return authenticatedToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private String determineUsername(CustomAuthenticationToken authentication) {

        if (authentication.getOauthId() == null) {
            throw new BadCredentialsException("OAUTH ID IS NULL");
        }

        return authentication.getOauthId();
    }

    //구글 인증 구현
    //이메일과 oauthId 비교
    private void checkGoogle(CustomUserDetails userDetails, CustomAuthenticationToken authentication) throws AuthenticationException {

        if (!userDetails.getOauthId().equals(authentication.getOauthId())
            //|| !userDetails.getEmail().equals(authentication.getEmail())
        ) {
            log.error("WRONG CREDENTIALS GOOGLE {} {}", authentication.getOauthId(), authentication.getEmail());
            throw new BadCredentialsException("WRONG GOOGLE OAUTH ID OR EMAIL");
        }
    }

    //카카오 인증 구현
    //oauthId만 비교 (카카오에서 받아올 수 있는 데이터 중 고유한 값이 없음)
    private void checkKakao(CustomUserDetails userDetails, CustomAuthenticationToken authentication) throws AuthenticationException {

        if (!userDetails.getOauthId().equals(authentication.getOauthId())) {
            log.error("WRONG CREDENTIALS KAKAO {}", authentication.getOauthId());
            throw new BadCredentialsException("WRONG KAKAO OAUTH ID");
        }
    }
}

