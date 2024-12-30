package org._1mg.tt_backend.auth.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

/**
 * 로그인 요청시 들어오는 데이터와 로그인 완료 후 데이터가 저장되는 DTO
 * 주목할 점은 principal에 들어가는 값이 다름 그래서 Object 형
 * - 인증 전 : 입력된 사용자 정보
 * - 인증 후 : 인증된 사용자 정보 (DB에서 조회한 값)
 */
@Getter
public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private final String oauthId;
    private final UUID memberId;
    private final String email;
    private final String oauthProvider;
    private final Object principal;

    public CustomAuthenticationToken(String oauthId, String email, String oauthProvider, Object principal) {
        super(null);
        this.oauthId = oauthId;
        this.memberId = null;
        this.email = email;
        this.oauthProvider = oauthProvider;
        this.principal = principal;
        this.setAuthenticated(false);
    }

    public CustomAuthenticationToken(UUID memberId, String email, String oauthProvider, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.oauthId = null;
        this.memberId = memberId;
        this.email = email;
        this.oauthProvider = oauthProvider;
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }


    public static CustomAuthenticationToken unauthenticated(String oauthId, String email, String oauthProvider, Object principal) {
        return new CustomAuthenticationToken(oauthId, email, oauthProvider, principal);
    }

    public static CustomAuthenticationToken authenticated(UUID memberId, String email, String oauthProvider, Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new CustomAuthenticationToken(memberId, email, oauthProvider, principal, authorities);
    }
}
