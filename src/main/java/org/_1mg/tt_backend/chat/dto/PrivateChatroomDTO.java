package org._1mg.tt_backend.chat.dto;

import lombok.*;
import org._1mg.tt_backend.auth.dto.ProfileDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivateChatroomDTO {

    private String chatroomId;
    private String title;
    private ProfileDTO user1;
    private ProfileDTO user2;
    private boolean alarm;
    private String imagePath;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private String lastReadMessageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
}
