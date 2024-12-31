package org._1mg.tt_backend.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Integer messageId;
    private Integer chatroomId;
    private String memberId;
    private String content;
    private String messageType; // "ENTER", "LEAVE", or "CHAT"
    private LocalDateTime createdAt = LocalDateTime.now(); // 메시지 생성 시간 추가
}
