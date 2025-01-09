package org._1mg.tt_backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org._1mg.tt_backend.chat.MessageType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnterDTO {

    private String profileId;
    private MessageType messageType;
}
