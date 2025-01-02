package org._1mg.tt_backend.landmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    private Double longitude;
    private Double latitude;
    private Integer radius;
}
