package org._1mg.tt_backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class TextDTO extends BaseMessage {

    private String messageId;
    private MessageType messageType;
    private String content;
    private List<ProfileDTO> profiles;
    private LocalDateTime createdAt;
}
