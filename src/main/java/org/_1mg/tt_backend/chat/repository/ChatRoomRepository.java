package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Integer> {
}