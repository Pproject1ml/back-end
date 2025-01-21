package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<ChatroomEntity, Long> {

    @Query("SELECT c FROM ChatroomEntity c " +
            "JOIN FETCH c.profileChatrooms pc " +
            "JOIN FETCH c.landmark l " +
            "JOIN FETCH pc.profile p " +
            "WHERE p.profileId = :profileId " +
            "AND c.isDeleted = false " +
            "AND pc.isDeleted = false " +
            "AND l.isDeleted = false")
    List<ChatroomEntity> findChatroomsByProfileIdNotDeleted(@Param("profileId") Long profileId);

    @Query("SELECT c FROM ChatroomEntity c " +
            "WHERE c.chatroomId = :chatroomId " +
            "AND c.isDeleted = false ")
    Optional<ChatroomEntity> findByIdNotDeleted(Long chatroomId);
}