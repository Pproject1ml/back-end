package org._1mg.tt_backend.exception;

import lombok.Getter;

@Getter
public enum CustomException {

    /**
     * 정상 응답
     */
    OK(20),

    /**
     * 기본 인증 관련 예외
     * 없는 회원
     * 로그인이 필요한 요청
     * 인증이 됐지만 권한이 없는 사용자
     */

    DEFAULT_AUTH_ERROR(30),
    NEED_SIGN_UP(31),
    NEED_SIGN_IN(32),
    UNAUTHORIZED(33),


    /**
     * 이미 존재하는 사용자
     * 중복된 닉네임
     */
    ALREADY_EXISTS_USER(41),
    ALREADY_EXISTS_NICKNAME(42),

    /**
     * 기본 토큰 관련 예외
     * 만료된 토큰 예외
     */
    DEFAULT_TOKEN_ERROR(50),
    EXPIRED_TOKEN(51),
    
    ;

    private final int status;

    CustomException(int status) {
        this.status = status;
    }
}
