package org._1mg.tt_backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    private Long chatroom;
    private Double longitude;
    private Double latitude;
    private Integer radius;
}
