package org._1mg.tt_backend.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomDTO {

    private Long chatroomId;
    private String title;
    private List<ChatMessageDTO> messages;
    private List<MemberChatDTO> members;
    private LocalDateTime createdAt;
}
