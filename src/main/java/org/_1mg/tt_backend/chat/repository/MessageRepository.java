package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.MessageEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m " +
            "WHERE m.chatroom.chatroomId = :chatroomId " +
            "AND m.isDeleted = false " +
            "ORDER BY m.createdAt DESC")
    MessageEntity findLastMessageWithChatroomNotDeleted(@Param("chatroomId") Long chatroomId, Limit limit);

    @Query("SELECT m FROM MessageEntity m " +
            "JOIN FETCH m.profile p " +
            "WHERE m.chatroom.chatroomId = :chatroomId " +
            "AND m.messageId > :startId AND m.messageId < :endId " +
            "AND m.isDeleted = false " +
            "ORDER BY m.messageId ASC")
    List<MessageEntity> findMessagesBetweenStartAndEndNotDeleted(@Param("chatroomId") Long chatroomId,
                                                                 @Param("startId") Long startId,
                                                                 @Param("endId") Long endId);

    @Query("SELECT m FROM MessageEntity m " +
            "JOIN FETCH m.profile p " +
            "WHERE m.chatroom.chatroomId = :chatroomId " +
            "AND m.messageId > :startId " +
            "AND m.isDeleted = false " +
            "ORDER BY m.messageId ASC")
    List<MessageEntity> findMessagesFromStartNotDeleted(@Param("chatroomId") Long chatroomId,
                                                        @Param("startId") Long startId);
}