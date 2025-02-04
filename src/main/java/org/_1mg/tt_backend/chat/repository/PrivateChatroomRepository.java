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
            "AND (pc.user1.profileId = :profileId OR pc.user2.profileId = :profileId) " +
            "AND pc.isDeleted = false ")
    Optional<PrivateChatroomEntity> findByIdAndUserNotDeleted(Long profileId, Long chatroomId);

    @Query("SELECT pc FROM PrivateChatroomEntity pc " +
            "LEFT JOIN FETCH pc.user1 u1 " +
            "LEFT JOIN FETCH pc.user2 u2 " +
            "WHERE (pc.user1.profileId = :profileId " +
            "OR pc.user2.profileId = :profileId) " +
            "AND pc.isDeleted = false ")
    List<PrivateChatroomEntity> findChatroomsByProfileIdNotDeleted(@Param("profileId") Long profileId);


    @Query(value = "SELECT pc.* FROM private_chatroom pc " +
            "WHERE pc.user1_id = :user1 " +
            "AND pc.user2_id = :user2 ", nativeQuery = true)
    PrivateChatroomEntity findByUser1AndUser2(Long user1, Long user2);

    @Query(value = "SELECT pc FROM PrivateChatroomEntity pc " +
            "LEFT JOIN FETCH pc.user1 u1 " +
            "LEFT JOIN FETCH pc.user2 u2 " +
            "WHERE (u1.profileId = :profileId OR u2.profileId = :profileId) " +
            "AND pc.isDeleted = false ")
    List<PrivateChatroomEntity> findAllUserPrivateChatrooms(Long profileId);

    @Query(value = "SELECT pc FROM PrivateChatroomEntity pc " +
            "LEFT JOIN FETCH pc.user1 u1 " +
            "LEFT JOIN FETCH pc.user2 u2 " +
            "WHERE pc.privateChatroomId = :chatroomId " +
            "AND pc.isDeleted = false ")
    PrivateChatroomEntity findProfilesById(Long chatroomId);
}
