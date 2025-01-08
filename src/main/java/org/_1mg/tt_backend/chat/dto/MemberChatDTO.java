package org._1mg.tt_backend.chat.dto;

import lombok.*;
import org._1mg.tt_backend.auth.dto.ProfileDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberChatDTO {

    private ProfileDTO profile;
    private ChatroomDTO chatRoom;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}
