package org._1mg.tt_backend.chat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
public class LeaveDTO extends BaseMessage {

    private LocalDateTime leftAt;
}
