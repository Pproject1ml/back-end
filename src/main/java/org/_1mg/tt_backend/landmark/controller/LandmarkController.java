package org._1mg.tt_backend.landmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;
import org._1mg.tt_backend.landmark.dto.LocationDTO;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org._1mg.tt_backend.landmark.service.LandmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org._1mg.tt_backend.base.CustomException.OK;

@Slf4j
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

    // 랜드마크 생성 -> 채팅방 생성
    @PostMapping("/landmark")
    public ResponseDTO<LandmarkDTO> createLandmark(@RequestBody LandmarkDTO landmarkDTO) {
        log.info("landmarkDTO: {}", landmarkDTO.toString());
        // 랜드마크 및 채팅방 생성 서비스 호출
        Landmark savedLandmark = landmarkService.saveWithChatroom(landmarkDTO);

        return ResponseDTO.<LandmarkDTO>builder()
                .status(OK.getStatus()) // 성공 상태 코드
                .message("Landmark and Chatroom 생성 성공") // 응답 메시지
                .data(savedLandmark.convertToDTO()) // 저장된 랜드마크 정보를 반환
                .build();
    }

    // 랜드마크 삭제 -> 채팅방 삭제
    @DeleteMapping("/landmark/{id}")
    public ResponseDTO<String> deleteLandmark(@PathVariable Long id) {
        // 삭제된 랜드마크의 제목 반환
        String deletedLandmarkName = landmarkService.deleteLandmark(id);

        return ResponseDTO.<String>builder()
                .status(HttpStatus.OK.value()) // 성공 상태 코드
                .message("Landmark 삭제 성공")
                .data("Deleted Landmark : " + deletedLandmarkName) // 삭제된 랜드마크 제목 반환
                .build();
    }
}
