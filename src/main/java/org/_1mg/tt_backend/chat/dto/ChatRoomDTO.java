package org._1mg.tt_backend.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {
    private Integer chatroomId;
    private String landmarkName;
    private LocalDateTime createdAt;
//    private Boolean isActive;
}
