package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.MessageEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m " +
            "WHERE m.chatroom.chatroomId = :chatroomId " +
            "ORDER BY m.createdAt DESC")
    MessageEntity findLastMessageWithChatroom(@Param("chatroomId") Long chatroomId, Limit limit);
}