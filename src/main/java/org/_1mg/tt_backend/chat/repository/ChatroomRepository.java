package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<ChatroomEntity, Long> {

    @Query("SELECT c FROM ChatroomEntity c " +
            "WHERE c.chatroomId = :chatroomId " +
            "AND c.isDeleted = false ")
    Optional<ChatroomEntity> findByIdNotDeleted(Long chatroomId);
}