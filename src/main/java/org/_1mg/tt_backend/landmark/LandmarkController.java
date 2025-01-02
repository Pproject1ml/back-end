package org._1mg.tt_backend.landmark;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;
import org._1mg.tt_backend.landmark.dto.LocationDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org._1mg.tt_backend.exception.CustomException.OK;

@RestController
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;

    @GetMapping("/landmark")
    public ResponseDTO<List<LandmarkDTO>> getLandmarks(@RequestParam("longitude") Double longitude,
                                                       @RequestParam("latitude") Double latitude,
                                                       @RequestParam("radius") Integer radius) {

        LocationDTO location = LocationDTO.builder()
                .longitude(longitude)
                .latitude(latitude)
                .radius(radius)
                .build();

        List<LandmarkDTO> result = landmarkService.getLandmarks(location);

        return ResponseDTO.<List<LandmarkDTO>>builder()
                .status(OK.getStatus())
                .message("NEARBY LANDMARK FOR " + location.getLatitude() + "," + location.getLongitude())
                .data(result)
                .build();
    }

    @PostMapping("/landmark")
    public ResponseDTO<String> createLandmark(@RequestBody LandmarkDTO landmark) {

        landmarkService.save(landmark);

        return ResponseDTO.<String>builder()
                .status(OK.getStatus())
                .message("INPUT SUCCESS")
                .build();
    }
}
