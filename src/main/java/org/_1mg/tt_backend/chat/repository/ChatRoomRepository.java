package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatroomEntity, Long> {
    //Optional<ChatRoomEntity> findByLandmark_LandmarkId(Long landmarkId);
}