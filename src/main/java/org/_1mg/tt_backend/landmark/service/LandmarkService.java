package org._1mg.tt_backend.landmark.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.service.ChatroomService;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;
import org._1mg.tt_backend.landmark.dto.LocationDTO;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org._1mg.tt_backend.landmark.repository.LandmarkRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org._1mg.tt_backend.base.CustomException.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final ChatroomService chatroomService;
    private final ChatroomRepository chatroomRepository;

    // 랜드마크 목록
    public List<LandmarkDTO> getLandmarks(LocationDTO location) {

        // 위도 및 경도 범위 검증
        validateCoordinates(location.getLatitude(), location.getLongitude());

        // 페이징 정보 설정 (최대 50개)
        Pageable pageable = PageRequest.of(0, 50);

        // JPA에서 필터링, 정렬, 제한, 채팅방 조인까지 처리
        return landmarkRepository.findNearbyLandmarksWithChatroom(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getRadius(),
                        pageable
                ).stream()
                .map(Landmark::convertToDTO) // DTO 변환
                .collect(Collectors.toList());
    }

    // 위도 경도 검증
    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(INVALID_LATITUDE.getMessage());
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(INVALID_LONGITUDE.getMessage());
        }
    }

    public boolean isWithinRadius(double centralLat, double centralLon, double targetLat, double targetLon, double radiusM) {

        final double R = 6371.0; // Earth's radius in kilometers

        double lat1Rad = Math.toRadians(centralLat);
        double lon1Rad = Math.toRadians(centralLon);
        double lat2Rad = Math.toRadians(targetLat);
        double lon2Rad = Math.toRadians(targetLon);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceM = R * c * 1000;

        return distanceM <= radiusM;
    }

    /**
     * 랜드마크 생성 시 자동으로 채팅방 생성
     *
     * @param landmarkDTO 랜드마크 데이터 (위치, 이름, 반경 등 정보를 포함)
     * @return 생성된 랜드마크 엔터티
     */
    // 랜드마크 개별 추가
    @Transactional
    public Landmark saveWithChatroom(LandmarkDTO landmarkDTO) {
        // 랜드마크 필수 값 검증
        if (landmarkDTO.getName() == null || landmarkDTO.getLatitude() == null || landmarkDTO.getLongitude() == null) {
            throw new IllegalArgumentException(LANDMARK_MISSING_REQUIRED_FIELDS.getMessage());
        }
        // 위도와 경도 검증
        validateCoordinates(landmarkDTO.getLatitude(), landmarkDTO.getLongitude());

        // 1. 위도와 경도를 기준으로 랜드마크 존재 여부 확인
        Optional<Landmark> existingLandmark = landmarkRepository.findByLatitudeAndLongitude(
                landmarkDTO.getLatitude(),
                landmarkDTO.getLongitude()
        );

        if (existingLandmark.isPresent()) {
            Landmark landmark = existingLandmark.get();
            if (!landmark.isDeleted()) {
                throw new IllegalArgumentException(LANDMARK_ALREADY_EXISTS.getMessage());
            }

            // 2. 랜드마크가 삭제 상태일 경우 is_deleted를 false로 변경
            landmark.deleteFalse();

            // 3. 연관된 채팅방도 is_deleted를 false로 변경
            ChatroomEntity chatroom = landmark.getChatroom();
            if (chatroom != null) {
                if (!chatroom.isDeleted()) {
                    throw new IllegalArgumentException(LANDMARK_ALREADY_EXISTS.getMessage());
                }
                chatroom.deleteFalse();
                chatroomRepository.save(chatroom); // 채팅방 변경 사항 저장
            }

            return landmarkRepository.save(landmark); // 기존 랜드마크 복구
        }

        // 4. 새 랜드마크 생성
        Landmark newLandmark = Landmark.create(
                landmarkDTO.getName(),
                landmarkDTO.getLatitude(),
                landmarkDTO.getLongitude(),
                landmarkDTO.getAddress(),
                landmarkDTO.getRadius(),
                landmarkDTO.getImagePath()
        );

        Landmark savedLandmark = landmarkRepository.save(newLandmark);

        // 5. 새 채팅방 생성 및 랜드마크와 연관
        ChatroomEntity chatroom = chatroomService.createChatroomForLandmark(savedLandmark);
        savedLandmark.assignChatroom(chatroom);

        return landmarkRepository.save(savedLandmark); // 최종 저장
    }

    // 랜드마크 여러개 추가
    @Transactional
    public List<Landmark> saveMultipleWithChatrooms(List<LandmarkDTO> landmarkDTOs) {
        List<Landmark> savedLandmarks = new ArrayList<>();

        for (LandmarkDTO landmarkDTO : landmarkDTOs) {
            try {
                // 기존 단일 저장 로직을 재사용
                Landmark savedLandmark = saveWithChatroom(landmarkDTO);
                savedLandmarks.add(savedLandmark); // 성공한 랜드마크만 리스트에 추가
            } catch (IllegalArgumentException ex) {
                // 실패한 항목은 로그로 기록하고 처리 건너뜀
                log.warn("Failed to save landmark: {}", ex.getMessage());
            }
        }

        return savedLandmarks; // 저장된 랜드마크 리스트 반환
    }

    // 랜드마크 삭제
    @Transactional
    public String deleteLandmark(Long id) {
        // 1. 랜드마크 조회
        Landmark landmark = landmarkRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new IllegalArgumentException(LANDMARK_NOT_FOUND.getMessage()));

        // 2. is_deleted 상태 확인
        if (landmark.isDeleted()) {
            throw new IllegalStateException(LANDMARK_ALREADY_DELETED.getMessage());
        }

        // 3. 랜드마크 삭제 처리
        landmark.deleteTrue();

        // 4. 연관된 채팅방 삭제 처리
        ChatroomEntity chatroom = landmark.getChatroom();
        if (chatroom != null) {
            if (chatroom.isDeleted()) {
                throw new IllegalStateException(CHATROOM_ALREADY_DELETED.getMessage());
            }

            // 채팅방을 삭제 상태로 변경
            chatroom.deleteTrue();

            // 채팅방 변경 사항 저장
            chatroomRepository.save(chatroom);

            landmarkRepository.save(landmark); // 랜드마크 변경 사항 저장
        }

        // 삭제된 랜드마크의 제목 반환
        return landmark.getName();
    }

    public void checkLocation(org._1mg.tt_backend.chat.dto.LocationDTO location) {

        Landmark landmark = landmarkRepository.findByChatroomId(location.getChatroom());
        double centralLat = location.getLatitude();
        double centralLon = location.getLongitude();
        Integer radius = location.getRadius();

        if (!isWithinRadius(
                centralLat,
                centralLon,
                landmark.getLatitude(),
                landmark.getLongitude(),
                radius)) {
            throw new IllegalArgumentException(LANDMARK_INVALID_LOCATION.getMessage());
        }
    }
}

