package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.PrivateMessageEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessageEntity, Long> {

    @Query("SELECT pm FROM PrivateMessageEntity pm " +
            "WHERE pm.chatroom.privateChatroomId = :chatroomId " +
            "AND pm.isDeleted = false " +
            "ORDER BY pm.createdAt DESC")
    PrivateMessageEntity findLastMessageWithChatroomNotDeleted(@Param("chatroomId") Long chatroomId, Limit limit);


    @Query("SELECT pm FROM PrivateMessageEntity pm " +
            "JOIN FETCH pm.profile p " +
            "WHERE pm.chatroom.privateChatroomId = :chatroomId " +
            "AND pm.privateMessageId > :startId AND pm.privateMessageId < :endId " +
            "AND pm.isDeleted = false " +
            "ORDER BY pm.privateMessageId ASC")
    List<PrivateMessageEntity> findMessagesBetweenStartAndEndNotDeleted(@Param("chatroomId") Long chatroomId,
                                                                        @Param("startId") Long startId,
                                                                        @Param("endId") Long endId);

    @Query("SELECT pm FROM PrivateMessageEntity pm " +
            "JOIN FETCH pm.profile p " +
            "WHERE pm.chatroom.privateChatroomId = :chatroomId " +
            "AND pm.privateMessageId > :startId " +
            "AND pm.isDeleted = false " +
            "ORDER BY pm.privateMessageId ASC")
    List<PrivateMessageEntity> findMessagesFromStartNotDeleted(@Param("chatroomId") Long chatroomId,
                                                               @Param("startId") Long startId);

    @Query(value = "SELECT * from private_message m " +
            "WHERE m.profile_id = :profileId"
            , nativeQuery = true)
    List<PrivateMessageEntity> findAllMessages(Long profileId);

    @Query(value = "SELECT pm FROM PrivateMessageEntity pm " +
            "JOIN FETCH pm.chatroom c " +
            "JOIN FETCH pm.profile p " +
            "WHERE c.privateChatroomId = :chatroomId " +
            "AND pm.isDeleted = false " +
            "ORDER BY pm.privateMessageId ASC ")
    List<PrivateMessageEntity> findAllMessagesAtChatroom(Long chatroomId);

    @Query(value = "SELECT pm FROM PrivateMessageEntity pm " +
            "JOIN FETCH pm.chatroom c " +
            "JOIN FETCH pm.profile p " +
            "WHERE c.privateChatroomId = :chatroomId " +
            "AND pm.privateMessageId < :endId " +
            "AND pm.isDeleted = false " +
            "ORDER BY pm.privateMessageId ASC ")
    List<PrivateMessageEntity> findMessagesUntilEnd(Long chatroomId, Long endId);
}
