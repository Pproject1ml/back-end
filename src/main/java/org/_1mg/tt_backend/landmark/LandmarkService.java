package org._1mg.tt_backend.landmark;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org._1mg.tt_backend.chat.repository.ChatroomRepository;
import org._1mg.tt_backend.landmark.dto.LandmarkDTO;
import org._1mg.tt_backend.landmark.dto.LocationDTO;
import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final ChatroomRepository chatroomRepository; // 채팅방 저장소 주입

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
        // 1. 랜드마크 엔티티 생성
        Landmark landmark = Landmark.builder()
                .name(landmarkDTO.getName())
                .latitude(landmarkDTO.getLatitude())
                .longitude(landmarkDTO.getLongitude())
                .radius(landmarkDTO.getRadius())
                .imagePath(landmarkDTO.getImagePath())
                .build();

        // 2. 랜드마크 저장 (채팅방 정보 없이 먼저 저장)
        Landmark savedLandmark = landmarkRepository.save(landmark);

        // 3. 해당 랜드마크와 연결된 채팅방 생성
        ChatroomEntity chatroom = ChatroomEntity.builder()
                .title("Chatroom for " + savedLandmark.getName())
                .build();

        // 4. 채팅방 저장
        ChatroomEntity savedChatroom = chatroomRepository.save(chatroom);

        // 5. 랜드마크와 채팅방 간 관계 설정
        savedLandmark.assignChatroom(savedChatroom);

        // 6. 연관 관계 업데이트 후 다시 저장
        return landmarkRepository.save(savedLandmark); // 최종 저장 후 반환
    }
}

