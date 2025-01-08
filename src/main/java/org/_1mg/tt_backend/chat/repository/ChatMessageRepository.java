package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    //List<ChatMessageEntity> findByChatroomChatroomId(Long chatroomId);
}