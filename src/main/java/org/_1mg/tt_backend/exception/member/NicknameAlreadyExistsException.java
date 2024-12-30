package org._1mg.tt_backend.exception.member;

import lombok.Getter;

@Getter
public class NicknameAlreadyExistsException extends RuntimeException {

    private final String invalidNickname;

    public NicknameAlreadyExistsException(String message, String invalidNickname) {
        super(message);
        this.invalidNickname = invalidNickname;
    }
}
