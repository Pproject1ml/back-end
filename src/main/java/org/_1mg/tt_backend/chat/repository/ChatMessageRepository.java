package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {
    List<ChatMessageEntity> findByChatRoomChatroomId(Integer chatroomId);

    List<ChatMessageEntity> findByChatRoomChatroomIdAndIsReadFalse(Integer chatroomId);
}