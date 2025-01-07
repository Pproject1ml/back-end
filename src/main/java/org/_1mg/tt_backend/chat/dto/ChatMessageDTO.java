package org._1mg.tt_backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private Long messageId;
    private Long chatroomId;
    private String memberId;
    private String content;
    private String messageType; // "ENTER", "LEAVE", or "CHAT"
    private LocalDateTime createdAt = LocalDateTime.now(); // 메시지 생성 시간 추가
}
