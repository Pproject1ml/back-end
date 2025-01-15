package org._1mg.tt_backend.landmark.repository;

import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LandmarkRepository extends JpaRepository<Landmark, Integer> {
    // 위도와 경도로 랜드마크를 조회
    Optional<Landmark> findByLatitudeAndLongitude(Double latitude, Double longitude);

    @Query(value = "SELECT * " +
            "FROM landmark " +
            "WHERE chatroom_id = :chatroomId"
            , nativeQuery = true)
    Landmark findByChatroomId(@Param("chatroomId") Long ChatroomId);
}
