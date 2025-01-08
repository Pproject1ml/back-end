package org._1mg.tt_backend.chat.dto;

import lombok.*;
import org._1mg.tt_backend.auth.dto.ProfileDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomDTO {

    private Long chatroomId;
    private String title;
    private Map<Long, List<ProfileDTO>> profiles;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private String lastReadMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
