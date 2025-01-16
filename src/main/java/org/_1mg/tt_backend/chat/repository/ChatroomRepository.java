package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatroomRepository extends JpaRepository<ChatroomEntity, Long> {

    @Query("SELECT c FROM ChatroomEntity c " +
            "JOIN FETCH c.profileChatrooms pc " +
            "JOIN FETCH c.landmark l " +
            "JOIN FETCH pc.profile p " +
            "WHERE p.profileId = :profileId")
    List<ChatroomEntity> findChatroomsByProfileId(@Param("profileId") Long profileId);
}