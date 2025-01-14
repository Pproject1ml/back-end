package org._1mg.tt_backend.landmark.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.service.ChatroomService;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;
import org._1mg.tt_backend.landmark.dto.LocationDTO;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org._1mg.tt_backend.landmark.repository.LandmarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final ChatroomService chatroomService;

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
        //    이미 존재하는 랜드마크가 있으면 예외를 던져 중복 생성 방지
        landmarkRepository.findByLatitudeAndLongitude(
                        landmarkDTO.getLatitude(),
                        landmarkDTO.getLongitude())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("이미 존재하는 랜드마크입니다.");
                });

        // 2. 랜드마크 엔터티 생성
        //    전달받은 데이터를 기반으로 랜드마크 객체를 생성
        Landmark landmark = Landmark.create(
                landmarkDTO.getName(),
                landmarkDTO.getLatitude(),
                landmarkDTO.getLongitude(),
                landmarkDTO.getRadius(),
                landmarkDTO.getImagePath()
        );

        // 3. 랜드마크 저장
        //    먼저 랜드마크를 데이터베이스에 저장 (채팅방 정보는 아직 포함되지 않음)
        Landmark savedLandmark = landmarkRepository.save(landmark);

        // 4. 채팅방 생성
        //    랜드마크와 연관된 채팅방을 ChatroomService를 통해 생성
        ChatroomEntity chatroom = chatroomService.createChatroomForLandmark(savedLandmark);

        // 5. 랜드마크와 채팅방 간 관계 설정
        //    생성된 채팅방을 랜드마크와 연결 (양방향 관계 설정이 필요한 경우 사용)
        savedLandmark.assignChatroom(chatroom);

        // 6. 연관 관계 업데이트 후 랜드마크 최종 저장
        //    채팅방과 연관 관계가 포함된 상태로 랜드마크를 데이터베이스에 최종 저장
        return landmarkRepository.save(savedLandmark);
    }
}

