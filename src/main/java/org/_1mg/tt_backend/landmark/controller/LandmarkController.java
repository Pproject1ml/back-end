package org._1mg.tt_backend.landmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.base.CustomException;
import org._1mg.tt_backend.base.ResponseDTO;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;
import org._1mg.tt_backend.landmark.dto.LocationDTO;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org._1mg.tt_backend.landmark.service.LandmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org._1mg.tt_backend.base.CustomException.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;

    // 위도, 경도 검증
    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(CustomException.INVALID_LATITUDE.getMessage());
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(CustomException.INVALID_LONGITUDE.getMessage());
        }
    }

    @GetMapping("/landmark")
    public ResponseDTO<List<LandmarkDTO>> getLandmarks(@RequestParam("longitude") Double longitude,
                                                       @RequestParam("latitude") Double latitude,
                                                       @RequestParam("radius") Integer radius) {

        LocationDTO location = LocationDTO.builder()
                .longitude(longitude)
                .latitude(latitude)
                .radius(radius)
                .build();

        // 위도 및 경도 범위 검증
        validateCoordinates(latitude, longitude);

        // 가까운 랜드마크 목록 가져오기 (최대 50개)
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
                .message(OK.getMessage()) // 응답 메시지
                .data(savedLandmark.convertToDTO()) // 저장된 랜드마크 정보를 반환
                .build();
    }

    // 다중 랜드마크 추가
    @PostMapping("/landmarks")
    public ResponseDTO<List<LandmarkDTO>> createLandmarks(@RequestBody List<LandmarkDTO> landmarkDTOs) {
        log.info("Multiple landmarkDTOs: {}", landmarkDTOs.toString());

        // 여러 랜드마크 저장
        List<Landmark> savedLandmarks = landmarkService.saveMultipleWithChatrooms(landmarkDTOs);

        // 저장된 랜드마크를 DTO로 변환 후 반환
        List<LandmarkDTO> result = savedLandmarks.stream()
                .map(Landmark::convertToDTO)
                .collect(Collectors.toList());

        return ResponseDTO.<List<LandmarkDTO>>builder()
                .status(OK.getStatus()) // 성공 상태 코드
                .message(OK.getMessage()) // 응답 메시지
                .data(result) // 저장된 랜드마크 리스트 반환
                .build();
    }

    // 랜드마크 삭제 -> 채팅방 삭제 (is_deleted=true)
    @DeleteMapping("/landmark/{id}")
    public ResponseDTO<String> deleteLandmark(@PathVariable Long id) {
        // 삭제된 랜드마크의 제목 반환
        String deletedLandmarkName = landmarkService.deleteLandmark(id);

        return ResponseDTO.<String>builder()
                .status(HttpStatus.OK.value()) // 성공 상태 코드
                .message(OK.getMessage())
                .data("Deleted Landmark : " + deletedLandmarkName) // 삭제된 랜드마크 제목 반환
                .build();
    }
}
