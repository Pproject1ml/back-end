package org._1mg.tt_backend.landmark;

import lombok.RequiredArgsConstructor;
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

    public void save(LandmarkDTO landmarkDTO) {

        landmarkRepository.save(
                Landmark.builder()
                        .name(landmarkDTO.getName())
                        .latitude(landmarkDTO.getLatitude())
                        .longitude(landmarkDTO.getLongitude())
                        .radius(landmarkDTO.getRadius())
                        .imagePath(landmarkDTO.getImagePath())
                        //.chatRoomId(landmarkDTO.getChatRoomId())
                        .build()
        );
    }
}

