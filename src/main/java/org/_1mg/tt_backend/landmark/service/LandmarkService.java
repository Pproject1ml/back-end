package org._1mg.tt_backend.landmark.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.chat.service.ChatroomService;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;
import org._1mg.tt_backend.landmark.dto.LocationDTO;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org._1mg.tt_backend.landmark.repository.LandmarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final ChatroomService chatroomService;
    private final ChatroomRepository chatroomRepository;

    public List<LandmarkDTO> getLandmarks(LocationDTO location) {

        double centralLat = location.getLatitude();
        double centralLon = location.getLongitude();
        Integer radius = location.getRadius();

        return landmarkRepository.findAll().stream()
                .filter(landmark -> isWithinRadius(
                        centralLat,
                        centralLon,
                        landmark.getLatitude(),
                        landmark.getLongitude(),
                        radius))
                .map(Landmark::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean isWithinRadius(double centralLat, double centralLon, double targetLat, double targetLon, double radiusKm) {

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

        double distanceKm = R * c;

        return distanceKm <= radiusKm;
    }

    /**
     * 랜드마크 생성 시 자동으로 채팅방 생성
     *
     * @param landmarkDTO 랜드마크 데이터 (위치, 이름, 반경 등 정보를 포함)
     * @return 생성된 랜드마크 엔터티
     */
    @Transactional
    public Landmark saveWithChatroom(LandmarkDTO landmarkDTO) {
        // 랜드마크 필수 값 검증
        if (landmarkDTO.getName() == null || landmarkDTO.getLatitude() == null || landmarkDTO.getLongitude() == null) {
            throw new IllegalArgumentException("랜드마크 생성 시 필수 데이터가 누락되었습니다.");
        }
        // 1. 위도와 경도를 기준으로 랜드마크 존재 여부 확인
        Optional<Landmark> existingLandmark = landmarkRepository.findByLatitudeAndLongitude(
                landmarkDTO.getLatitude(),
                landmarkDTO.getLongitude()
        );

        if (existingLandmark.isPresent()) {
            Landmark landmark = existingLandmark.get();
            if (!landmark.isDeleted()) {
                throw new IllegalArgumentException("이미 생성된 랜드마크입니다.");
            }

            // 2. 랜드마크가 삭제 상태일 경우 is_deleted를 false로 변경
            landmark.deleteFalse();

            // 3. 연관된 채팅방도 is_deleted를 false로 변경
            ChatroomEntity chatroom = landmark.getChatroom();
            if (chatroom != null) {
                if (!chatroom.isDeleted()) {
                    throw new IllegalArgumentException("이미 생성된 채팅방입니다.");
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
                landmarkDTO.getRadius(),
                landmarkDTO.getImagePath()
        );

        Landmark savedLandmark = landmarkRepository.save(newLandmark);

        // 5. 새 채팅방 생성 및 랜드마크와 연관
        ChatroomEntity chatroom = chatroomService.createChatroomForLandmark(savedLandmark);
        savedLandmark.assignChatroom(chatroom);

        return landmarkRepository.save(savedLandmark); // 최종 저장
    }

    // 랜드마크 삭제
    @Transactional
    public String deleteLandmark(Long id) {
        // 1. 랜드마크 조회
        Landmark landmark = landmarkRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 랜드마크입니다."));

        // 2. is_deleted 상태 확인
        if (landmark.isDeleted()) {
            throw new IllegalStateException("이미 삭제된 랜드마크입니다.");
        }

        // 3. 랜드마크 삭제 처리
        landmark.deleteTrue();

        // 4. 연관된 채팅방 삭제 처리
        ChatroomEntity chatroom = landmark.getChatroom();
        if (chatroom != null) {
            if (chatroom.isDeleted()) {
                throw new IllegalStateException("채팅방은 이미 삭제된 상태입니다.");
            }
            chatroom.deleteTrue();
            chatroomRepository.save(chatroom); // 채팅방 변경 사항 저장
        }

        landmarkRepository.save(landmark); // 랜드마크 변경 사항 저장

        // 삭제된 랜드마크의 제목 반환
        return landmark.getName();
    }
}

