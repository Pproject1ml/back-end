package org._1mg.tt_backend.exception;

import lombok.Getter;

@Getter
public enum CustomException {

    /**
     * 정상 응답
     */
    OK(20, "정상 응답"),

    /**
     * 기본 인증 관련 예외
     * 없는 회원
     * 로그인이 필요한 요청
     * 인증이 됐지만 권한이 없는 사용자
     */

    DEFAULT_AUTH_ERROR(30, "인증 과정에서 에러가 발생했습니다"),
    NEED_SIGN_UP(31, "회원 가입이 필요합니다"),
    NEED_SIGN_IN(32, "로그인이 필요합니다"),
    UNAUTHORIZED(33, "권한이 없습니다"),


    /**
     * 이미 존재하는 사용자
     * 중복된 닉네임
     */
    ALREADY_EXISTS_USER(41, "이미 회원가입이 된 계정입니다"),
    ALREADY_EXISTS_NICKNAME(42, "이미 존재하는 닉네임입니다"),

    /**
     * 기본 토큰 관련 예외
     * 만료된 토큰 예외
     */
    DEFAULT_TOKEN_ERROR(50, "토큰 관련 에러가 발생했습니다"),
    EXPIRED_TOKEN(51, "만료된 토큰입니다"),

    ;

    private final int status;
    private final String message;

    CustomException(int status, String message) {

        this.status = status;
        this.message = message;
    }
}
