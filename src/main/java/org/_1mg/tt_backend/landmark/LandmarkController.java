package org._1mg.tt_backend.landmark;

import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;
import org._1mg.tt_backend.landmark.dto.LocationDTO;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org._1mg.tt_backend.base.CustomException.OK;

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
    public ResponseDTO<LandmarkDTO> createLandmark(@RequestBody LandmarkDTO landmarkDTO) {
        // 랜드마크 및 채팅방 생성 서비스 호출
        Landmark savedLandmark = landmarkService.saveWithChatroom(landmarkDTO);

        return ResponseDTO.<LandmarkDTO>builder()
                .status(OK.getStatus()) // 성공 상태 코드
                .message("Landmark and Chatroom created successfully") // 응답 메시지
                .data(savedLandmark.convertToDTO()) // 저장된 랜드마크 정보를 반환
                .build();
    }
}
