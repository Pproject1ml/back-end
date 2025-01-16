package org._1mg.tt_backend.landmark.dto;


import lombok.*;
import org._1mg.tt_backend.chat.dto.ChatroomDTO;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LandmarkDTO {

    private String landmarkId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private String imagePath;
    private ChatroomDTO chatroom;

}
