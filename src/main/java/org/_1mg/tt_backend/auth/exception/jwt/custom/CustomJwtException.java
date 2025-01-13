package org._1mg.tt_backend.auth.exception.jwt.custom;

import org.springframework.security.core.AuthenticationException;

/**
 * JWT 관련 예외
 * JWT에서 발생하는 예외는 AuthenticationException이 아니라 원하는 곳에서 처리하기 어려움
 * - 토큰 만료 : Entry에서 다뤄야 함
 * - 그 외 여러 토큰 관련 :
 */
public class CustomJwtException extends AuthenticationException {

    public CustomJwtException(String msg) {
        super(msg);
    }
}
