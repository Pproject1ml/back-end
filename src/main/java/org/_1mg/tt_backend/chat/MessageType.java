package org._1mg.tt_backend.chat;

import lombok.Getter;

@Getter
public enum MessageType {

    JOIN("JOIN"), //가입
    ENTER("ENTER"), //입장
    DIE("DIE"), //탈퇴
    LEAVE("LEAVE"), //퇴장
    DISABLE("DISABLE"), //범위를 벗어나서 비활성화
    TEXT("TEXT"), //메세지
    IMAGE("IMAGE"),
    DATE("DATE"),
    ERROR("ERROR"),
    ;

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public static MessageType getMessageType(String input) {

        for (MessageType messageType : MessageType.values()) {
            if (messageType.name().equals(input)) {
                return messageType;
            }
        }

        return null;
    }
}
