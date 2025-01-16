package org._1mg.tt_backend.landmark.repository;

import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandmarkRepository extends JpaRepository<Landmark, Integer> {
    // 위도와 경도로 랜드마크를 조회
    Optional<Landmark> findByLatitudeAndLongitude(Double latitude, Double longitude);

    // 사용자 위치에서 가까운 순으로 50개 정렬하며 is_deleted가 false인 데이터만 가져옴
    // 반경 내 랜드마크 조회 및 채팅방 정보 조인
    @Query("""
            SELECT l FROM Landmark l
            LEFT JOIN FETCH l.chatroom c
            WHERE l.isDeleted = FALSE
              AND FUNCTION('ST_Distance_Sphere',
                           POINT(l.longitude, l.latitude),
                           POINT(:longitude, :latitude)) <= :radius
            ORDER BY FUNCTION('ST_Distance_Sphere',
                              POINT(l.longitude, l.latitude),
                              POINT(:longitude, :latitude)) ASC
            """)
    List<Landmark> findNearbyLandmarksWithChatroom(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Integer radius,
            Pageable pageable
    );


    @Query(value = "SELECT * " +
            "FROM landmark " +
            "WHERE chatroom_id = :chatroomId"
            , nativeQuery = true)
    Landmark findByChatroomId(@Param("chatroomId") Long ChatroomId);

}
