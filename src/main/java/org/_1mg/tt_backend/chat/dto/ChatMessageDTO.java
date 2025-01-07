package org._1mg.tt_backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org._1mg.tt_backend.chat.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private Long messageId;
    private ChatroomDTO chatroom;
    private MemberDTO member;
    private String content;
    private MessageType messageType;
    private LocalDateTime createdAt; // 메시지 생성 시간 추가
}
