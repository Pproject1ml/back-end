package org._1mg.tt_backend.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivateChatroomDTO {

    private String chatroomId;
    private String title;
    private boolean alarm;
    private String imagePath;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private String lastReadMessageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
