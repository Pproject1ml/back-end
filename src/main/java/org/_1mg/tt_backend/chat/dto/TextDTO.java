package org._1mg.tt_backend.chat.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org._1mg.tt_backend.chat.MessageType;

import java.time.LocalDateTime;

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
}
