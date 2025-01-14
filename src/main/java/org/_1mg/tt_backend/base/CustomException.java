package org._1mg.tt_backend.base;

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
     * 올바르지 않은 JWT 서명
     */
    DEFAULT_TOKEN_ERROR(50, "토큰 관련 에러가 발생했습니다"),
    EXPIRED_TOKEN(51, "만료된 토큰입니다"),
    SIGNATURE_INVALID(52, "JWT 서명이 일치하지 않습니다"),


    /**
     * 랜드마크 관련 예외
     */
    LANDMARK_NOT_FOUND(60, "존재하지 않는 랜드마크입니다."),
    LANDMARK_ALREADY_EXISTS(61, "이미 생성된 랜드마크입니다."),
    LANDMARK_ALREADY_DELETED(62, "이미 삭제된 랜드마크입니다."),
    LANDMARK_MISSING_REQUIRED_FIELDS(63, "랜드마크 생성 시 필수 데이터가 누락되었습니다."),
    LANDMARK_ALREADY_HAS_CHATROOM(64, "이미 채팅방이 연결된 랜드마크입니다."),
    LANDMARK_ILLEGAL_ARGUMENT(90, "랜드마크 관련 잘못된 요청이 전달되었습니다."),
    LANDMARK_GENERAL_ERROR(91, "랜드마크 관련 내부 서버 오류가 발생했습니다."),

    /**
     * 채팅방 관련 예외
     */
    CHATROOM_ALREADY_DELETED(70, "이미 삭제된 채팅방입니다."),
    CHATROOM_ALREADY_EXISTS(71, "이미 생성된 채팅방입니다.");

    private final int status;
    private final String message;

    CustomException(int status, String message) {

        this.status = status;
        this.message = message;
    }
}
