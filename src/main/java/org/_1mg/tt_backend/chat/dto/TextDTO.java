package org._1mg.tt_backend.chat.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org._1mg.tt_backend.auth.dto.ProfileDTO;
import org._1mg.tt_backend.chat.MessageType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class TextDTO extends BaseMessage {

    private String messageId;
    private MessageType messageType;
    private String content;
    private LocalDateTime createdAt;

    //구독 처리할 때만 쓰이는 필드
    private List<ProfileDTO> profiles;
}
