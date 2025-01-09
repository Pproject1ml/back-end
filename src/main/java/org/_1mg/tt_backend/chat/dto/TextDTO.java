package org._1mg.tt_backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org._1mg.tt_backend.chat.MessageType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextDTO {

    private String profileId;
    private String chatroomId;
    private MessageType messageType;
    private String content;
    private LocalDateTime createdAt;
}
