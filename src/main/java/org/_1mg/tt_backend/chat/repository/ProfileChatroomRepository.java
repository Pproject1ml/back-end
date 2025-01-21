package org._1mg.tt_backend.chat.repository;

import org._1mg.tt_backend.chat.entity.ProfileChatroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileChatroomRepository extends JpaRepository<ProfileChatroomEntity, Long> {

    @Query(value = "SELECT * " +
            "FROM profile_chatroom " +
            "WHERE profile_id = :profileId " +
            "AND chatroom_id = :chatroomId " +
            "AND is_deleted = false"
            , nativeQuery = true)
    ProfileChatroomEntity findByProfileIdAndChatroomIdNotDeleted(@Param("profileId") Long profileId, @Param("chatroomId") Long chatroomId);
}