package org._1mg.tt_backend.landmark.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LandmarkDTO {

    private String name;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private String imagePath;
    private Integer chatRoomId;
}
