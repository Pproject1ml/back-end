package org._1mg.tt_backend.chat.dto;

import lombok.*;
import org._1mg.tt_backend.chat.MessageType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinDTO {

    private String profileId;
    private String chatroomId;
    private MessageType messageType;
    private String message;
}
