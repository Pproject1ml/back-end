package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.PrivateChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrivateChatroomRepository extends JpaRepository<PrivateChatroomEntity, Long> {

    @Query("SELECT pc FROM PrivateChatroomEntity pc " +
            "WHERE pc.privateChatroomId = :chatroomId " +
            "AND pc.user1 = :profileId OR pc.user2 = :profileId " +
            "AND pc.isDeleted = false ")
    Optional<PrivateChatroomEntity> findByIdAndUserNotDeleted(Long profileId, Long chatroomId);

    @Query("SELECT pc FROM ProfileChatroomEntity pc " +
            "JOIN FETCH pc.chatroom c " +
            "JOIN FETCH c.landmark l " +
            "JOIN FETCH pc.profile p " +
            "WHERE p.profileId = :profileId " +
            "AND c.isDeleted = false " +
            "AND pc.isDeleted = false " +
            "AND l.isDeleted = false")
    List<PrivateChatroomEntity> findChatroomsByProfileIdNotDeleted(@Param("profileId") Long profileId);
}
