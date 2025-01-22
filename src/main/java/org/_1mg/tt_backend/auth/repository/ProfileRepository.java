package org._1mg.tt_backend.auth.repository;

import org._1mg.tt_backend.auth.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Profile findByNickname(String username);

    @Query("SELECT DISTINCT p FROM Profile p " +
            "JOIN FETCH p.profileChatrooms pc " +
            "JOIN FETCH pc.chatroom c " +
            "WHERE c.chatroomId = :chatroomId " +
            "AND p.isDeleted = false " +
            "AND pc.active = true")
    List<Profile> findProfilesByChatroomIdNotDeletedAndActive(Long chatroomId);

    @Query("SELECT p FROM Profile p " +
            "WHERE p.isDeleted = false " +
            "AND p.profileId = :profileId ")
    Optional<Profile> findByIdNotDeleted(Long profileId);
}
