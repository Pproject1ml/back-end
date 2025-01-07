package org._1mg.tt_backend.chat;

import lombok.Getter;

@Getter
public enum MessageType {

    JOIN("JOIN"), //가입
    ENTER("ENTER"), //입장
    DIE("DIE"), //탈퇴
    LEAVE("LEAVE"), //퇴장
    MESSAGE("MESSAGE"), //메세지
    ;

    private final String value;

    MessageType(String value) {
        this.value = value;
    }
}
