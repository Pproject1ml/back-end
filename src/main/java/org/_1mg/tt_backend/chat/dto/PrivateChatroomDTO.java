package org._1mg.tt_backend.chat.dto;

import lombok.*;
import org._1mg.tt_backend.auth.dto.ProfileDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PrivateChatroomDTO {

    private String chatroomId;
    private String title;
    private List<ProfileDTO> profiles;
    private boolean alarm;
    private String imagePath;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private String lastReadMessageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
}
